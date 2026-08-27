package com.kltyton.mob_battle.items.armor.compressarmor;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static com.kltyton.mob_battle.items.armor.compressarmor.CompressArmorSkillSupport.dust;
import static com.kltyton.mob_battle.items.armor.compressarmor.CompressArmorSkillSupport.spawnArmorBurst;
import static com.kltyton.mob_battle.items.armor.compressarmor.CompressArmorSkillSupport.spawnGroundRing;
import static com.kltyton.mob_battle.items.armor.compressarmor.DiamondNetheriteArmorSkills.COLOR_NETHERITE_PURPLE;

/**
 * 钻石/下界合金压缩护甲拉扯技能的粒子特效模块。
 * 负责拉扯开始、拉扯连线、目标标记与标记破碎四类无状态粒子表现，
 * 由 {@link DiamondNetheriteArmorSkills} 在拉扯技能状态机的对应阶段调用；
 * 本类不持有可变状态，粒子参数、分支与调用顺序与迁移前 Manager 实现完全一致。
 */
final class DiamondNetheritePullSkill {
    private DiamondNetheritePullSkill() {
    }

    static void spawnPullStartParticles(ServerLevel world, ServerPlayer player, Vector3f color, boolean netherite) {
        Vec3 center = player.position().add(0.0D, 0.1D, 0.0D);

        spawnGroundRing(world, center, 5.0D, color, 120, netherite ? 1.25F : 1.05F);
        spawnGroundRing(world, center, 2.6D, netherite ? COLOR_NETHERITE_PURPLE : color, 72, 0.9F);
        spawnArmorBurst(world, player.position().add(0.0D, 1.0D, 0.0D), color, netherite ? 1.45F : 1.2F, netherite ? 80 : 56, 1.15D);

        if (netherite) {
            world.sendParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY(0.55D), player.getZ(), 90, 1.2D, 0.8D, 1.2D, 0.08D);
            world.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY(0.45D), player.getZ(), 45, 1.0D, 0.4D, 1.0D, 0.035D);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, 0.75F, 0.65F);
        } else {
            world.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY(0.55D), player.getZ(), 70, 1.15D, 0.8D, 1.15D, 0.25D);
            world.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getY(0.6D), player.getZ(), 30, 0.7D, 0.45D, 0.7D, 0.08D);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 0.9F, 1.5F);
        }
    }

    static void spawnPullLineParticles(ServerLevel world, Vec3 from, Vec3 to, Vector3f color, boolean netherite) {
        Vec3 delta = to.subtract(from);
        int points = Math.max(6, (int) (delta.length() * 4.0D));

        for (int i = 0; i <= points; i++) {
            double progress = (double) i / (double) points;
            Vec3 pos = from.add(delta.scale(progress));

            world.sendParticles(dust(color, netherite ? 1.15F : 0.95F), pos.x, pos.y, pos.z, 2, 0.035D, 0.035D, 0.035D, 0.01D);

            if (i % 4 == 0) {
                world.sendParticles(netherite ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.ENCHANT, pos.x, pos.y, pos.z, 1, 0.02D, 0.02D, 0.02D, 0.03D);
            }
        }
    }

    static void spawnMarkParticles(ServerLevel world, LivingEntity target, Vector3f color, boolean netherite) {
        Vec3 center = target.position().add(0.0D, target.getBbHeight() * 0.55D, 0.0D);
        double radius = 0.75D;
        int points = 42;

        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0D * i / points;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;

            world.sendParticles(dust(color, 0.95F), x, center.y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        world.sendParticles(netherite ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.ENCHANT, center.x, center.y, center.z, netherite ? 28 : 34, 0.35D, 0.5D, 0.35D, netherite ? 0.08D : 0.25D);
    }

    static void spawnMarkBreakParticles(ServerLevel world, LivingEntity target, Vector3f color, boolean netherite) {
        Vec3 center = target.position().add(0.0D, target.getBbHeight() * 0.55D, 0.0D);

        world.sendParticles(dust(color, netherite ? 1.55F : 1.35F), center.x, center.y, center.z, 64, 0.6D, 0.7D, 0.6D, 0.12D);
        world.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.sendParticles(netherite ? ParticleTypes.SMOKE : ParticleTypes.ELECTRIC_SPARK, center.x, center.y, center.z, 28, 0.45D, 0.45D, 0.45D, 0.08D);
    }
}
