package com.kltyton.mob_battle.entity.deepcreature.skill;

import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Heightmap;

import java.util.List;

public class SkillUtils {
    /* -------------------------
     * 📦 通用工具方法区域
     * -------------------------
     */

    /** 获取半径范围内存活的玩家 */
    public static List<PlayerEntity> getNearbyPlayers(DeepCreatureEntity entity, double radius) {
        return entity.getWorld().getEntitiesByClass(
                PlayerEntity.class,
                entity.getBoundingBox().expand(radius),
                p -> p.isAlive() && entity.distanceTo(p) <= radius
        );
    }

    /** 对玩家施加击退力 */
    public static void knockbackPlayer(DeepCreatureEntity entity,
                                        PlayerEntity player,
                                        double horizPower,
                                        double vertBase,
                                        double vertRand) {
        if (player.isSpectator() || player.isCreative()) return;

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
        player.addVelocity(dx * horizPower, vy, dz * horizPower);
        player.velocityModified = true;
    }

    /** 生成范围内的粒子特效 */
    public static void spawnParticles(ServerWorld world, DeepCreatureEntity entity, int count, double range) {
        for (int i = 0; i < count; i++) {
            double px = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * range;
            double pz = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * range;
            double py = entity.getY() + 0.2;
            world.spawnParticles(ParticleTypes.CRIT, px, py, pz, 1, 0, 0, 0, 0);
        }
    }
    public static void spawnEvokerFangsRing(ServerWorld world, DeepCreatureEntity entity, double radius, double spacing, double delayPerRing) {
        // 环形生成尖牙：从近到远
        for (double r = 2.0; r <= radius; r += spacing) {
            int count = (int) (Math.PI * 2 * r / spacing);
            for (int i = 0; i < count; i++) {
                double angle = (2 * Math.PI * i) / count;
                double x = entity.getX() + Math.cos(angle) * r;
                double z = entity.getZ() + Math.sin(angle) * r;
                double y = world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int)x, (int)z);

                EvokerFangsEntity fangs = new EvokerFangsEntity(world, x, y, z, (float) angle, (int) (r / spacing * delayPerRing * 10), entity);
                world.spawnEntity(fangs);
            }
        }
    }

}
