package com.kltyton.mob_battle.entity.deepcreature.skill;

import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.levelgen.Heightmap;

public class SkillUtils {
    /* -------------------------
     * 📦 通用工具方法区域
     * -------------------------
     */

    /** 获取半径范围内存活的玩家 */
    public static List<LivingEntity> getNearbyPlayers(DeepCreatureEntity entity, double radius) {
        return entity.level().getEntitiesOfClass(
                LivingEntity.class,
                entity.getBoundingBox().inflate(radius),
                p -> EntityUtil.isValidCombatTarget(entity, p) && entity.distanceTo(p) <= radius
        );
    }

    /** 对玩家施加击退力 */
    public static void knockbackPlayer(DeepCreatureEntity entity,
                                       LivingEntity player,
                                        double horizPower,
                                        double vertBase,
                                        double vertRand) {
        if (player instanceof Player p) {
            if (p.isSpectator() || p.isCreative()) return;
        }

        double dx = player.getX() - entity.getX();
        double dz = player.getZ() - entity.getZ();
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 0.001) {
            dx = (entity.getRandom().nextDouble() - 0.5);
            dz = (entity.getRandom().nextDouble() - 0.5);
            len = Math.sqrt(dx * dx + dz * dz);
        }

        dx /= len;
        dz /= len;
        double vy = vertBase + entity.getRandom().nextDouble() * vertRand;
        player.push(dx * horizPower, vy, dz * horizPower);
        player.hurtMarked = true;
    }

    /** 生成范围内的粒子特效 */
    public static void spawnParticles(ServerLevel world, DeepCreatureEntity entity, int count, double range) {
        for (int i = 0; i < count; i++) {
            double px = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * range;
            double pz = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * range;
            double py = entity.getY() + 0.2;
            world.sendParticles(ParticleTypes.CRIT, px, py, pz, 1, 0, 0, 0, 0);
        }
    }
    public static void spawnEvokerFangsRing(ServerLevel world, DeepCreatureEntity entity, double radius, double spacing, double delayPerRing) {
        // 环形生成尖牙：从近到远
        for (double r = 2.0; r <= radius; r += spacing) {
            int count = (int) (Math.PI * 2 * r / spacing);
            for (int i = 0; i < count; i++) {
                double angle = (2 * Math.PI * i) / count;
                double x = entity.getX() + Math.cos(angle) * r;
                double z = entity.getZ() + Math.sin(angle) * r;
                double y = world.getHeight(Heightmap.Types.MOTION_BLOCKING, (int)x, (int)z);

                EvokerFangs fangs = new EvokerFangs(world, x, y, z, (float) angle, (int) (r / spacing * delayPerRing * 10), entity);
                world.addFreshEntity(fangs);
            }
        }
    }

}
