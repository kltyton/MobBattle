package com.kltyton.mob_battle.mixin.entity.boss.dragon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.end.DragonRespawnStage;
import net.minecraft.world.level.dimension.end.EnderDragonFight;

@Mixin(EnderDragonFight.class)
public abstract class EnderDragonFightMixin {

    @Shadow private DragonRespawnStage respawnStage;
    @Shadow private ServerBossEvent dragonEvent;
    @Shadow private ServerLevel level;
    @Shadow private List<EntityReference<EndCrystal>> respawnCrystals;
    @Shadow private BlockPos exitPortalLocation;
    @Shadow private boolean hasPreviouslyKilledDragon;

    // Unique 字段模拟数据包中的 trueEnding_storage
    @Unique
    private int customRespawnTimer = -1;
    @Unique
    private boolean wasPreviouslyKilledBeforeDragonKilled = false;

    @Inject(method = "updateDragon", at = @At("TAIL"))
    private void updateDragonBossBarName(EnderDragon dragon, CallbackInfo ci) {
        updateDragonBossBarName(dragon);
    }

    @Inject(method = "setDragonKilled", at = @At("HEAD"))
    private void rememberPreviouslyKilled(EnderDragon dragon, CallbackInfo ci) {
        this.wasPreviouslyKilledBeforeDragonKilled = this.hasPreviouslyKilledDragon;
    }

    @Inject(method = "setDragonKilled", at = @At("TAIL"))
    private void spawnEggForRespawnedDragon(EnderDragon dragon, CallbackInfo ci) {
        if (!this.wasPreviouslyKilledBeforeDragonKilled) {
            return;
        }
        BlockPos base = this.exitPortalLocation != null ? this.exitPortalLocation : BlockPos.ZERO;
        BlockPos eggPos = base.above(4);
        if (this.level.getBlockState(eggPos).isAir()) {
            this.level.setBlockAndUpdate(eggPos, Blocks.DRAGON_EGG.defaultBlockState());
        }
    }

    @Unique
    private void updateDragonBossBarName(EnderDragon dragon) {
        if (dragon == null) {
            return;
        }

        int health = Math.max(0, (int) Math.ceil(dragon.getHealth()));
        int maxHealth = Math.max(1, (int) Math.ceil(dragon.getMaxHealth()));
        this.dragonEvent.setName(dragon.getDisplayName().copy().append(" | " + health + "/" + maxHealth));
    }

    @Unique
    private List<EndCrystal> mobBattle$getRespawnCrystals() {
        if (this.respawnCrystals == null) {
            return List.of();
        }
        return this.respawnCrystals.stream()
                .map(reference -> reference.getEntity(this.level, EndCrystal.class))
                .filter(Objects::nonNull)
                .toList();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void injectCustomRespawnEffects(CallbackInfo ci) {
        if (this.respawnStage == null) {
            customRespawnTimer = -1;
            return;
        }

        if (customRespawnTimer == -1) customRespawnTimer = 0;
        customRespawnTimer++;

        int t = customRespawnTimer;
        BlockPos pos = this.exitPortalLocation != null ? this.exitPortalLocation : BlockPos.ZERO;
        boolean is10Tick = t % 10 == 0;

        // --- 1. 定时激活音效 (respawning/set.mcfunction) ---
        // 每 40 tick (100, 140... 500) 触发一次
        if (t >= 100 && t <= 500 && (t - 100) % 40 == 0) {
            level.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 4.0f, 1.2f);
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS, 4.0f, 0.5f);
        }

        // --- 2. 向内聚集的粒子环 (wave_particle3) ---
        // 每 10 tick 触发，从半径 5 处向水晶中心收缩
        if (is10Tick && t >= 60 && t <= 580 && this.respawnCrystals != null) {
            for (EndCrystal crystal : mobBattle$getRespawnCrystals()) {
                spawnGatheringRing(level, crystal.getX(), crystal.getY() + 0.2, crystal.getZ());
            }
        }
        // --- 氛围音效与逻辑 (根据数据包 a_main.mcfunction) ---

        // 400..580 周期性氛围音 (15% 几率)
        if (t >= 400 && t <= 580) {
            if (level.getRandom().nextFloat() < 0.15f) {
                level.playSound(null, pos, SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS.value(), SoundSource.AMBIENT, 6.0f, 0.9f);
            }
        }

        // 500 tick 强化音效 (数据包里写了三次播放，模拟叠加感)
        if (t == 500) {
            for (int i = 0; i < 3; i++) {
                level.playSound(null, pos, SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS.value(), SoundSource.AMBIENT, 6.0f, 0.9f);
            }
            level.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 4.0f, 0.5f);
        }

        // 525 tick 钟声
        if (t == 525) {
            level.playSound(null, pos, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 4.0f, 0.5f);
            level.playSound(null, pos, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 4.0f, 0.5f);
        }

        // --- 粒子效果逻辑 ---

        // 40 tick 以上：水晶周围冒烟
        if (t >= 40 && this.respawnCrystals != null) {
            for (EndCrystal crystal : mobBattle$getRespawnCrystals()) {
                level.sendParticles(net.minecraft.core.particles.PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0F), crystal.getX(), crystal.getY() + 1, crystal.getZ(), 2, 0, 0, 0, 0.05);
            }
        }

        // 15 tick：激活效果 (Wave2 + 激活音)
        if (t == 15) {
            level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 4.0f, 1.0f);
            level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 4.0f, 0.65f);
            if (this.respawnCrystals != null) {
                for (EndCrystal crystal : mobBattle$getRespawnCrystals()) {
                    spawnWaveParticle2(level, crystal.getX(), crystal.getY() + 1.0, crystal.getZ());
                }
            }
        }

        // 60..580 tick 周期性扩散 (每10 tick 触发 Wave3)
        if (is10Tick && t >= 60 && t <= 580 && this.respawnCrystals != null) {
            for (EndCrystal crystal : mobBattle$getRespawnCrystals()) {
                spawnWaveParticle3(level, crystal.getX(), crystal.getY(), crystal.getZ());
            }
        }

        // 545..590+ 高空中心粒子 (Y+62)
        if (t >= 545) {
            double topY = pos.getY() + 62;
            level.sendParticles(net.minecraft.core.particles.PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0F), pos.getX(), topY, pos.getZ(), 3, 0, 0, 0, 0.3);
            if (t >= 590) {
                level.sendParticles(ParticleTypes.END_ROD, pos.getX(), topY, pos.getZ(), 3, 0, 0, 0, 0.3);
            }
        }

        // 597..598 闪光
        if (t >= 597 && t <= 598 && this.respawnCrystals != null) {
            for (EndCrystal crystal : mobBattle$getRespawnCrystals()) {
                level.sendParticles(net.minecraft.core.particles.ColorParticleOption.create(ParticleTypes.FLASH, 0xFFFFFF), crystal.getX(), crystal.getY(), crystal.getZ(), 4, 0, 0, 0, 0.2);
            }
        }

        // 600 tick 最终冲天光束 (末地烛)
        if (t == 600 && this.respawnCrystals != null) {
            for (EndCrystal crystal : mobBattle$getRespawnCrystals()) {
                level.sendParticles(ParticleTypes.END_ROD, crystal.getX(), crystal.getY() + 50, crystal.getZ(), 50, 0, 50, 0, 0.4);
            }
        }
        // --- 4. 最终复活大场面 (respawning/end.mcfunction) ---
        if (t == 601) {
            triggerFinalExplosion(pos);
        }
    }

    // --- 粒子数学实现部分 ---

    @Unique
    private void spawnWaveParticle2(ServerLevel world, double x, double y, double z) {
        // 对应数据包 wave_particle2: 72次递归, 每次5度, 半径0.5
        for (int i = 0; i < 72; i++) {
            double rad = Math.toRadians(i * 5.0);
            double px = x - Math.sin(rad) * 0.5;
            double pz = z + Math.cos(rad) * 0.5;
            world.sendParticles(net.minecraft.core.particles.PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0F), px, y, pz, 1, 0, 0, 0, 0);
        }
    }

    @Unique
    private void spawnWaveParticle3(ServerLevel world, double x, double y, double z) {
        // 对应数据包 wave_particle3: 36次递归, 每次10度, 半径5.0
        for (int i = 0; i < 36; i++) {
            double rad = Math.toRadians(i * 10.0);
            double px = x - Math.sin(rad) * 5.0;
            double pz = z + Math.cos(rad) * 5.0;
            world.sendParticles(net.minecraft.core.particles.PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0F), px, y + 0.2, pz, 1, 0, 0, 0, 0);
        }
    }
    /**
     * 对应 wave_particle3: 实现从 5 格外向内“聚集”的效果
     */
    @Unique
    private void spawnGatheringRing(ServerLevel world, double x, double y, double z) {
        for (int i = 0; i < 36; i++) {
            double angle = Math.toRadians(i * 10.0);
            double offsetX = Math.cos(angle) * 5.0;
            double offsetZ = Math.sin(angle) * 5.0;

            // 粒子生成在 offsetX 处，速度设为负值，使其向中心飞行
            // 参数：粒子类型, x, y, z, 数量, 速度X, 速度Y, 速度Z, 速度倍率
            world.sendParticles(net.minecraft.core.particles.PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0F),
                    x + offsetX, y, z + offsetZ,
                    0, -offsetX * 0.1, 0, -offsetZ * 0.1, 0.5);
        }
    }

    /**
     * 对应 end.mcfunction: 最终的音效叠加和粒子爆发
     */
    @Unique
    private void triggerFinalExplosion(BlockPos pos) {
        if (this.respawnCrystals != null) {
            for (EndCrystal crystal : mobBattle$getRespawnCrystals()) {
                // 玻璃碎裂效果
                level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GLASS.defaultBlockState()),
                        crystal.getX(), crystal.getY() + 1, crystal.getZ(), 50, 0.3, 0.5, 0.3, 0.4);

                // 音效叠加
                level.playSound(null, crystal.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 3.0f, 0.6f);
                level.playSound(null, crystal.blockPosition(), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 4.0f, 0.6f);
                level.playSound(null, crystal.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 6.0f, 0.8f);

                // 复活时向上蔓延的线 (用 End Rod 模拟)
                for (double h = 0; h < 60; h += 2) {
                    level.sendParticles(ParticleTypes.END_ROD, crystal.getX(), crystal.getY() + h, crystal.getZ(), 2, 0.1, 0.1, 0.1, 0.05);
                }
            }
        }
    }
}
