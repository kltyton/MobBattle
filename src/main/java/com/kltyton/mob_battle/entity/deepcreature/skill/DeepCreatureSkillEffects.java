package com.kltyton.mob_battle.entity.deepcreature.skill;

import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * 深渊生物技能的目标查询、击退和服务端效果实现。
 *
 * <p>所有方法都必须在实体所属世界线程调用；粒子和尖牙会直接修改服务端世界。</p>
 */
public final class DeepCreatureSkillEffects {
    private DeepCreatureSkillEffects() {
    }

    /** 获取半径内可被深渊生物攻击的存活目标。 */
    public static List<LivingEntity> getNearbyTargets(DeepCreatureEntity entity, double radius) {
        return entity.level().getEntitiesOfClass(
                LivingEntity.class,
                entity.getBoundingBox().inflate(radius),
                p -> EntityQueries.isValidCombatTarget(entity, p) && entity.distanceTo(p) <= radius
        );
    }

    /** 对目标施加水平击退和随机垂直速度，并排除创造或旁观玩家。 */
    public static void knockbackTarget(DeepCreatureEntity entity,
                                       LivingEntity target,
                                        double horizPower,
                                        double vertBase,
                                        double vertRand) {
        if (target instanceof Player p) {
            if (p.isSpectator() || p.isCreative()) return;
        }

        double dx = target.getX() - entity.getX();
        double dz = target.getZ() - entity.getZ();
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 0.001) {
            dx = (entity.getRandom().nextDouble() - 0.5);
            dz = (entity.getRandom().nextDouble() - 0.5);
            len = Math.sqrt(dx * dx + dz * dz);
        }

        dx /= len;
        dz /= len;
        double vy = vertBase + entity.getRandom().nextDouble() * vertRand;
        target.push(dx * horizPower, vy, dz * horizPower);
        target.hurtMarked = true;
    }

    /** 在实体周围随机发送服务端暴击粒子。 */
    public static void spawnParticles(ServerLevel world, DeepCreatureEntity entity, int count, double range) {
        for (int i = 0; i < count; i++) {
            double px = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * range;
            double pz = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * range;
            double py = entity.getY() + 0.2;
            world.sendParticles(ParticleTypes.CRIT, px, py, pz, 1, 0, 0, 0, 0);
        }
    }
    /** 由内向外生成带递增启动延迟的唤魔者尖牙环。 */
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
