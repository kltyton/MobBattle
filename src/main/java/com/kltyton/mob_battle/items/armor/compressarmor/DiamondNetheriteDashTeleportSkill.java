package com.kltyton.mob_battle.items.armor.compressarmor;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static com.kltyton.mob_battle.items.armor.compressarmor.CompressArmorSkillSupport.dust;
import static com.kltyton.mob_battle.items.armor.compressarmor.CompressArmorSkillSupport.spawnGroundRing;
import static com.kltyton.mob_battle.items.armor.compressarmor.DiamondNetheriteArmorSkills.COLOR_NETHERITE;
import static com.kltyton.mob_battle.items.armor.compressarmor.DiamondNetheriteArmorSkills.COLOR_NETHERITE_PURPLE;

/**
 * COMPRESSED_DIAMOND / COMPRESSED_NETHERITE 套装冲刺与传送技能的纯粒子特效与终点计算模块。
 * 冲刺碰撞终点、冲刺充能/轨迹/扫刃/收尾粒子以及下界合金传送双端粒子均在此提供，
 * 由 {@link DiamondNetheriteArmorSkills} 在服务端技能流程中调用；方法体自
 * {@code CompressArmorSkillManager} 原样提取，仅调整可见性与依赖引用，不持有任何可变状态。
 */
final class DiamondNetheriteDashTeleportSkill {
    private DiamondNetheriteDashTeleportSkill() {
    }

    /**
     * 沿水平方向按 0.25 格步进寻找最后一个无碰撞位置作为冲刺终点。
     */
    static Vec3 findDashEnd(ServerPlayer player, Vec3 direction, double distance) {
        ServerLevel world = player.level();
        Vec3 start = player.position();
        Vec3 safeEnd = start;
        int steps = Math.max(1, Mth.ceil(distance / 0.25D));

        for (int i = 1; i <= steps; i++) {
            double stepDistance = distance * i / steps;
            Vec3 candidate = start.add(direction.scale(stepDistance));
            AABB candidateBox = player.getBoundingBox().move(candidate.subtract(start));
            if (!world.noCollision(player, candidateBox)) {
                break;
            }
            safeEnd = candidate;
        }

        return safeEnd;
    }

    /**
     * 冲刺起手充能粒子：dust 扩散 + 传送门/电火花点缀 + 地面双色圆环。
     */
    static void spawnDashChargeParticles(ServerLevel world, ServerPlayer player, Vector3f color, boolean netherite) {
        Vec3 center = player.position().add(0.0D, 1.0D, 0.0D);

        world.sendParticles(dust(color, netherite ? 1.45F : 1.2F), center.x, center.y, center.z, netherite ? 70 : 52, 0.6D, 0.75D, 0.6D, 0.08D);
        world.sendParticles(netherite ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.ELECTRIC_SPARK, center.x, center.y, center.z, netherite ? 55 : 36, 0.55D, 0.65D, 0.55D, netherite ? 0.08D : 0.1D);
        spawnGroundRing(world, player.position(), netherite ? 2.8D : 2.2D, color, netherite ? 90 : 72, netherite ? 1.25F : 1.0F);
    }

    /**
     * 冲刺路径拖尾粒子：主轨 dust 密铺 + 两侧 dust 副轨 + 间隔传送门/电火花点缀。
     */
    static void spawnDashTrailParticles(ServerLevel world, Vec3 start, Vec3 end, Vec3 direction, Vector3f color, boolean netherite) {
        Vec3 delta = end.subtract(start);
        int points = Math.max(8, (int) (delta.length() * 5.0D));
        Vec3 right = new Vec3(-direction.z, 0.0D, direction.x).normalize();

        for (int i = 0; i <= points; i++) {
            double progress = (double) i / (double) points;
            Vec3 base = start.add(delta.scale(progress)).add(0.0D, 0.18D, 0.0D);

            world.sendParticles(dust(color, netherite ? 1.35F : 1.1F), base.x, base.y, base.z, 6, 0.12D, 0.08D, 0.12D, 0.035D);

            Vec3 left = base.add(right.scale(-0.65D));
            Vec3 rightPos = base.add(right.scale(0.65D));

            world.sendParticles(dust(color, 0.9F), left.x, left.y, left.z, 2, 0.04D, 0.04D, 0.04D, 0.01D);
            world.sendParticles(dust(color, 0.9F), rightPos.x, rightPos.y, rightPos.z, 2, 0.04D, 0.04D, 0.04D, 0.01D);

            if (i % 3 == 0) {
                world.sendParticles(netherite ? ParticleTypes.SMOKE : ParticleTypes.ELECTRIC_SPARK, base.x, base.y + 0.1D, base.z, 3, 0.12D, 0.08D, 0.12D, 0.03D);
            }
        }
    }

    /**
     * 冲刺沿途扫刃粒子：按进度分布 SWEEP_ATTACK 与弧形 dust 刀刃，下界合金额外带传送门粒子。
     */
    static void spawnDashSweepBlades(ServerLevel world, Vec3 start, Vec3 end, Vec3 direction, Vector3f color, boolean netherite) {
        Vec3 delta = end.subtract(start);
        int blades = netherite ? 7 : 5;
        Vec3 right = new Vec3(-direction.z, 0.0D, direction.x).normalize();

        for (int i = 0; i < blades; i++) {
            double progress = (i + 0.5D) / blades;
            Vec3 center = start.add(delta.scale(progress)).add(0.0D, 1.0D, 0.0D);

            world.sendParticles(ParticleTypes.SWEEP_ATTACK, center.x, center.y, center.z, netherite ? 3 : 2, 0.18D, 0.08D, 0.18D, 0.0D);

            for (int j = -5; j <= 5; j++) {
                Vec3 blade = center
                        .add(right.scale(j * 0.28D))
                        .add(0.0D, Math.sin((j + 5) / 10.0D * Math.PI) * 0.35D, 0.0D);

                world.sendParticles(dust(color, netherite ? 1.35F : 1.15F), blade.x, blade.y, blade.z, 1, 0.015D, 0.015D, 0.015D, 0.0D);
            }

            if (netherite) {
                world.sendParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, 12, 0.45D, 0.25D, 0.45D, 0.06D);
            } else {
                world.sendParticles(ParticleTypes.ENCHANT, center.x, center.y, center.z, 10, 0.45D, 0.25D, 0.45D, 0.18D);
            }
        }
    }

    /**
     * 冲刺终点收尾爆发：大范围 dust + 扫击 + 烟雾/电火花，下界合金额外带传送门粒子。
     */
    static void spawnDashEndBurst(ServerLevel world, Vec3 end, Vector3f color, boolean netherite) {
        Vec3 center = end.add(0.0D, 1.0D, 0.0D);

        world.sendParticles(dust(color, netherite ? 1.55F : 1.25F), center.x, center.y, center.z, netherite ? 90 : 62, 0.75D, 0.75D, 0.75D, 0.11D);
        world.sendParticles(ParticleTypes.SWEEP_ATTACK, center.x, center.y, center.z, netherite ? 5 : 3, 0.55D, 0.2D, 0.55D, 0.0D);
        world.sendParticles(netherite ? ParticleTypes.SMOKE : ParticleTypes.ELECTRIC_SPARK, center.x, center.y, center.z, netherite ? 42 : 28, 0.65D, 0.45D, 0.65D, 0.08D);

        if (netherite) {
            world.sendParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, 70, 0.9D, 0.7D, 0.9D, 0.1D);
        } else {
            world.sendParticles(ParticleTypes.END_ROD, center.x, center.y, center.z, 24, 0.45D, 0.45D, 0.45D, 0.06D);
        }
    }

    /**
     * 下界合金传送起始端粒子：双环 + dust 爆发 + 传送门与烟雾。
     */
    static void spawnNetheriteTeleportStart(ServerLevel world, ServerPlayer player) {
        Vec3 center = player.position().add(0.0D, 1.0D, 0.0D);

        spawnGroundRing(world, player.position(), 2.2D, COLOR_NETHERITE, 96, 1.25F);
        spawnGroundRing(world, player.position(), 1.1D, COLOR_NETHERITE_PURPLE, 54, 1.0F);
        world.sendParticles(dust(COLOR_NETHERITE, 1.5F), center.x, center.y, center.z, 95, 0.75D, 0.9D, 0.75D, 0.1D);
        world.sendParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, 120, 0.9D, 1.0D, 0.9D, 0.12D);
        world.sendParticles(ParticleTypes.SMOKE, center.x, center.y - 0.2D, center.z, 40, 0.65D, 0.35D, 0.65D, 0.04D);
    }

    /**
     * 下界合金传送落点粒子：爆炸 + 双色 dust + 传送门与烟雾。
     */
    static void spawnNetheriteTeleportImpact(ServerLevel world, LivingEntity target) {
        Vec3 center = target.position().add(0.0D, target.getBbHeight() * 0.55D, 0.0D);

        world.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 2, 0.15D, 0.15D, 0.15D, 0.0D);
        world.sendParticles(dust(COLOR_NETHERITE, 1.65F), center.x, center.y, center.z, 120, 0.8D, 0.8D, 0.8D, 0.13D);
        world.sendParticles(dust(COLOR_NETHERITE_PURPLE, 1.2F), center.x, center.y, center.z, 70, 0.65D, 0.65D, 0.65D, 0.1D);
        world.sendParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, 95, 0.85D, 0.85D, 0.85D, 0.14D);
        world.sendParticles(ParticleTypes.SMOKE, center.x, center.y - 0.1D, center.z, 50, 0.55D, 0.45D, 0.55D, 0.05D);
    }
}
