package com.kltyton.mob_battle.items.armor.compressarmor;

import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.function.Predicate;

/**
 * 压缩护甲技能的跨模块共享无状态工具：冷却提示、视线目标查找、水平方向、
 * dust 粒子以及各护甲模块共用的 burst/ring/arc/hit spark 特效。
 * 本类只放被两个及以上护甲模块复用的纯函数，不持有任何可变状态。
 */
final class CompressArmorSkillSupport {
    private static final String TEXT_ARMOR_SKILL_COOLING_DOWN = "message.mob_battle.armor_skill_cooling_down";

    private CompressArmorSkillSupport() {
    }

    /**
     * 冷却判定：冷却中向玩家显示剩余秒数 HUD 并返回 true，否则返回 false。
     * 冷却文案与百分比换算与原 Manager 实现完全一致。
     */
    static boolean isCoolingDown(ServerPlayer player, ItemStack cooldownItem, int cooldownSeconds) {
        if (!player.getCooldowns().isOnCooldown(cooldownItem)) {
            return false;
        }

        float progress = player.getCooldowns().getCooldownPercent(cooldownItem, 0.0F);
        float remainingSeconds = progress * cooldownSeconds;

        player.sendOverlayMessage(Component.translatable(TEXT_ARMOR_SKILL_COOLING_DOWN, String.format("%.1f", remainingSeconds))
                .withStyle(ChatFormatting.RED));

        return true;
    }

    /**
     * 在玩家视线前方范围内查找最近的可战斗目标（无额外条件版本）。
     */
    static LivingEntity findTargetInSight(ServerPlayer player, double range) {
        return findTargetInSight(player, range, target -> true);
    }

    /**
     * 在玩家视线前方范围内查找最近的满足额外条件的可战斗目标。
     * 沿视线点积投影筛选并取最近目标，与原 Manager 实现一致。
     */
    static LivingEntity findTargetInSight(ServerPlayer player, double range, Predicate<LivingEntity> extraPredicate) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F).normalize();
        AABB box = player.getBoundingBox().expandTowards(look.scale(range)).inflate(2.0);
        LivingEntity best = null;
        double bestDistance = range + 1.0;

        for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, box, target ->
                EntityQueries.isValidCombatTarget(player, target)
                        && extraPredicate.test(target))) {
            Vec3 toTarget = target.getBoundingBox().getCenter().subtract(eye);
            double alongRay = toTarget.dot(look);

            if (alongRay < 0.0 || alongRay > range) {
                continue;
            }

            double distanceToRay = toTarget.subtract(look.scale(alongRay)).lengthSqr();

            if (distanceToRay <= 2.25 && alongRay < bestDistance) {
                best = target;
                bestDistance = alongRay;
            }
        }

        return best;
    }

    /**
     * 玩家视线方向去掉垂直分量后的单位水平方向；几乎垂直时回退到 +Z。
     */
    static Vec3 horizontalDirection(ServerPlayer player) {
        Vec3 direction = player.getViewVector(1.0F);
        Vec3 horizontal = new Vec3(direction.x, 0.0, direction.z);

        if (horizontal.lengthSqr() < 0.001) {
            return new Vec3(0.0, 0.0, 1.0);
        }

        return horizontal.normalize();
    }

    /**
     * 由 RGB 颜色与缩放构造 dust 粒子，颜色分量换算规则与原 Manager 一致。
     */
    static ParticleOptions dust(Vector3f color, float scale) {
        return new DustParticleOptions(
                ((int) (color.x * 255.0F) << 16)
                        | ((int) (color.y * 255.0F) << 8)
                        | (int) (color.z * 255.0F),
                scale
        );
    }

    /**
     * 护甲通用爆发粒子：dust 扩散 + END_ROD 点缀，参数与调用点保持一致。
     */
    static void spawnArmorBurst(ServerLevel world, Vec3 center, Vector3f color, float scale, int count, double spread) {
        world.sendParticles(dust(color, scale), center.x, center.y, center.z, count, spread, spread * 0.75D, spread, 0.08D);
        world.sendParticles(ParticleTypes.END_ROD, center.x, center.y, center.z, Math.max(6, count / 4), spread * 0.35D, spread * 0.55D, spread * 0.35D, 0.035D);
    }

    /**
     * 地面环形粒子：沿圆环撒 dust，每 6 个点附带一个 CRIT 点缀。
     */
    static void spawnGroundRing(ServerLevel world, Vec3 center, double radius, Vector3f color, int points, float scale) {
        double y = center.y + 0.08D;

        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0D * i / points;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;

            world.sendParticles(dust(color, scale), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);

            if (i % 6 == 0) {
                world.sendParticles(ParticleTypes.CRIT, x, y + 0.08D, z, 1, 0.02D, 0.02D, 0.02D, 0.03D);
            }
        }
    }

    /**
     * 玩家前方扇形弧线粒子，角度以视线水平方向为中心对称展开。
     */
    static void spawnForwardArc(ServerLevel world, Vec3 origin, Vec3 forward, Vector3f color, double radius, double arcDegrees, int points) {
        Vec3 horizontal = new Vec3(forward.x, 0.0D, forward.z).normalize();
        double baseAngle = Math.atan2(horizontal.z, horizontal.x);
        double halfArc = Math.toRadians(arcDegrees * 0.5D);

        for (int i = 0; i < points; i++) {
            double progress = points <= 1 ? 0.5D : (double) i / (double) (points - 1);
            double angle = baseAngle - halfArc + halfArc * 2.0D * progress;
            double x = origin.x + Math.cos(angle) * radius;
            double z = origin.z + Math.sin(angle) * radius;

            world.sendParticles(dust(color, 1.15F), x, origin.y, z, 1, 0.02D, 0.02D, 0.02D, 0.01D);
        }
    }

    /**
     * 命中火花：轻击/重击（heavy）两种强度，重击额外附带伤害指示粒子。
     */
    static void spawnHitSpark(ServerLevel world, LivingEntity target, Vector3f color, boolean heavy) {
        double x = target.getX();
        double y = target.getY(0.55D);
        double z = target.getZ();

        world.sendParticles(dust(color, heavy ? 1.35F : 1.0F), x, y, z, heavy ? 30 : 16, 0.35D, 0.45D, 0.35D, 0.07D);
        world.sendParticles(ParticleTypes.CRIT, x, y, z, heavy ? 18 : 8, 0.32D, 0.38D, 0.32D, 0.12D);

        if (heavy) {
            world.sendParticles(ParticleTypes.DAMAGE_INDICATOR, x, y + 0.2D, z, 10, 0.32D, 0.32D, 0.32D, 0.12D);
        }
    }
}
