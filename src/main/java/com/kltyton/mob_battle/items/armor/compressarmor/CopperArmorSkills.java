package com.kltyton.mob_battle.items.armor.compressarmor;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.network.packet.ParticleStormEmitterPayload;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import com.kltyton.mob_battle.event.scheduler.ServerTickScheduler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * COMPRESSED_COPPER 套装技能：雷击、飞踢、蓄力校验与 ParticleStorm emitter 特效。
 * 技能均要求蓄力效果，冷却、视线目标查找与通用粒子复用 {@link CompressArmorSkillSupport}。
 */
final class CopperArmorSkills {
    private static final String TEXT_COMPRESSED_COPPER_NOT_CHARGED = "message.mob_battle.compressed_copper_not_charged";
    private static final String TEXT_COMPRESSED_COPPER_NO_TARGET = "message.mob_battle.compressed_copper_no_target";

    private static final Vector3f COLOR_COPPER = new Vector3f(0.95F, 0.43F, 0.18F);

    private static final Identifier COMPRESSED_COPPER_LIGHTNING =
            Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "compressed_copper_lightning");
    private static final Identifier COMPRESSED_COPPER_LIGHTNING_BURST =
            Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "compressed_copper_lightning_burst");

    private CopperArmorSkills() {
    }

    /**
     * 雷击技能：蓄力校验后对视线目标落雷，6 tick 后对落点 3 格内敌人造成二次伤害
     * 并播放爆炸特效，最后进入 10 秒冷却。
     */
    static void runCopperLightningSkill(ServerPlayer player) {
        if (!hasCopperCharge(player)) return;

        ItemStack cooldownItem = new ItemStack(ModItems.COMPRESSED_COPPER_INGOT);
        if (CompressArmorSkillSupport.isCoolingDown(player, cooldownItem, 10)) return;

        LivingEntity target = CompressArmorSkillSupport.findTargetInSight(player, 24.0D);
        if (target == null) {
            player.sendOverlayMessage(Component.translatable(TEXT_COMPRESSED_COPPER_NO_TARGET).withStyle(ChatFormatting.RED));
            return;
        }

        ServerLevel world = player.level();
        Vec3 impact = target.position();
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world, EntitySpawnReason.EVENT);
        if (lightning != null) {
            lightning.setVisualOnly(true);
            lightning.setPos(target.getX(), target.getY(), target.getZ());
            world.addFreshEntity(lightning);
        }

        target.invulnerableTime = 0;
        target.hurtServer(world, player.damageSources().lightningBolt(), 5.0F);
        spawnCopperBurst(world, impact.add(0.0D, target.getBbHeight() * 0.5D, 0.0D), 36, 0.8D);

        ServerTickScheduler.schedule(world.getServer(), 6, () -> {
            if (player.isRemoved()) {
                return;
            }
            Vec3 center = target.isRemoved() ? impact : target.position();
            AABB hitBox = new AABB(center, center).inflate(3.0D);
            for (LivingEntity victim : world.getEntitiesOfClass(LivingEntity.class, hitBox, victim -> EntityQueries.isValidCombatTarget(player, victim))) {
                victim.invulnerableTime = 0;
                victim.hurtServer(world, player.damageSources().playerAttack(player), 25.0F);
                CompressArmorSkillSupport.spawnHitSpark(world, victim, COLOR_COPPER, false);
            }
            world.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y + 0.5D, center.z, 2, 0.1D, 0.1D, 0.1D, 0.0D);
            CompressArmorSkillSupport.spawnGroundRing(world, center, 3.0D, COLOR_COPPER, 72, 1.0F);
            world.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.9F, 1.2F);
        });

        player.getCooldowns().addCooldown(cooldownItem, 10 * 20);
    }

    /**
     * 飞踢技能：蓄力校验后冲向目标，8 tick 后传送至目标身侧并击飞，
     * 再 4 tick 后对目标造成伤害与 ParticleStorm 冲击特效，最后进入 10 秒冷却。
     */
    static void runCopperKickSkill(ServerPlayer player) {
        if (!hasCopperCharge(player)) return;

        ItemStack cooldownItem = new ItemStack(ModItems.COMPRESSED_COPPER_BOOTS);
        if (CompressArmorSkillSupport.isCoolingDown(player, cooldownItem, 10)) return;

        LivingEntity target = CompressArmorSkillSupport.findTargetInSight(player, 18.0D);
        if (target == null) {
            player.sendOverlayMessage(Component.translatable(TEXT_COMPRESSED_COPPER_NO_TARGET).withStyle(ChatFormatting.RED));
            return;
        }

        ServerLevel world = player.level();
        Vec3 start = player.position();
        Vec3 targetPos = target.position();
        player.setDeltaMovement(0.0D, 1.1D, 0.0D);
        player.hurtMarked = true;
        spawnCopperBurst(world, start.add(0.0D, 1.0D, 0.0D), 28, 0.5D);
        world.playSound(null, start.x, start.y, start.z, SoundEvents.TRIDENT_RIPTIDE_1.value(), SoundSource.PLAYERS, 0.9F, 1.25F);

        ServerTickScheduler.schedule(world.getServer(), 8, () -> {
            if (player.isRemoved() || !target.isAlive()) {
                return;
            }

            Vec3 direction = target.position().subtract(player.position());
            if (direction.lengthSqr() < 0.01D) {
                direction = targetPos.subtract(start);
            }
            if (direction.lengthSqr() < 0.01D) {
                direction = player.getViewVector(1.0F);
            }
            direction = direction.normalize();
            Vec3 kickDirection = direction;

            player.teleportTo(target.getX() - kickDirection.x * 1.2D, target.getY() + 2.2D, target.getZ() - kickDirection.z * 1.2D);
            player.setDeltaMovement(kickDirection.scale(1.35D).add(0.0D, -1.25D, 0.0D));
            player.hurtMarked = true;

            ServerTickScheduler.schedule(world.getServer(), 4, () -> {
                if (player.isRemoved() || !target.isAlive()) {
                    return;
                }
                target.invulnerableTime = 0;
                target.hurtServer(world, player.damageSources().playerAttack(player), 30.0F);
                target.setDeltaMovement(target.getDeltaMovement().add(kickDirection.scale(0.7D).add(0.0D, 0.45D, 0.0D)));
                target.hurtMarked = true;
                Vec3 impactGround = target.position().add(0.0D, 0.05D, 0.0D);
                spawnCompressedCopperImpactStorm(world, impactGround);
                spawnCopperBurst(world, target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D), 42, 0.65D);
                world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.75F, 1.45F);
            });
        });

        player.getCooldowns().addCooldown(cooldownItem, 10 * 20);
    }

    /**
     * 蓄力校验：拥有压缩铜蓄力效果时放行，否则显示未蓄力 HUD 并拒绝技能。
     */
    private static boolean hasCopperCharge(ServerPlayer player) {
        if (player.hasEffect(ModEffects.COMPRESSED_COPPER_CHARGED_ENTRY)) {
            return true;
        }
        player.sendOverlayMessage(Component.translatable(TEXT_COMPRESSED_COPPER_NOT_CHARGED).withStyle(ChatFormatting.RED));
        return false;
    }

    private static void spawnCopperBurst(ServerLevel world, Vec3 center, int count, double spread) {
        world.sendParticles(CompressArmorSkillSupport.dust(COLOR_COPPER, 1.15F), center.x, center.y, center.z, count, spread, spread, spread, 0.08D);
        world.sendParticles(ParticleTypes.ELECTRIC_SPARK, center.x, center.y, center.z, Math.max(8, count / 3), spread * 0.7D, spread * 0.7D, spread * 0.7D, 0.1D);
    }

    private static void spawnCompressedCopperImpactStorm(ServerLevel world, Vec3 center) {
        spawnParticleStormEmitter(world, center, COMPRESSED_COPPER_LIGHTNING);
        spawnParticleStormEmitter(world, center, COMPRESSED_COPPER_LIGHTNING_BURST);
    }

    /**
     * 向所有可接收该 Payload 的在线玩家广播 ParticleStorm emitter 生成包。
     */
    private static void spawnParticleStormEmitter(ServerLevel world, Vec3 center, Identifier particleId) {
        ParticleStormEmitterPayload payload = new ParticleStormEmitterPayload(particleId, center, -1);
        for (ServerPlayer viewer : world.players()) {
            if (ServerPlayNetworking.canSend(viewer, ParticleStormEmitterPayload.ID)) {
                ServerPlayNetworking.send(viewer, payload);
            }
        }
    }
}
