package com.kltyton.mob_battle.entity.drone.goal;

import com.kltyton.mob_battle.entity.drone.treatmentdrone.TreatmentDroneEntity;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class HealTeamGoal extends Goal {

    private final TreatmentDroneEntity drone;
    private final double speed;
    private LivingEntity nearestTeammate;

    public HealTeamGoal(TreatmentDroneEntity drone, double speed) {
        this.drone = drone;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // 没主人或没驯服就不治疗
        if (!drone.isTame() || drone.getOwner() == null) {
            return false;
        }

        // 找到最近的需要治疗的队友
        nearestTeammate = findNearestInjuredTeammate();
        return nearestTeammate != null;
    }

    @Override
    public boolean canContinueToUse() {
        return nearestTeammate != null && nearestTeammate.isAlive() && drone.distanceToSqr(nearestTeammate) > 2.0;
    }

    @Override
    public void start() {
        drone.getNavigation().moveTo(nearestTeammate, speed);
    }

    @Override
    public void tick() {
        if (nearestTeammate == null || !nearestTeammate.isAlive()) {
            return;
        }

        double distSq = drone.distanceToSqr(nearestTeammate);

        // 保持一个舒适的治疗距离（5~10格）
        if (distSq > 9.0) {
            drone.getNavigation().moveTo(nearestTeammate, speed);
        } else if (distSq < 2.0) {
            drone.getNavigation().stop();
        } else {
            drone.getNavigation().stop();
            drone.getLookControl().setLookAt(nearestTeammate, 30.0F, 30.0F);
        }

        if (--drone.healTickTimer <= 0) {
            drone.healTickTimer = TreatmentDroneEntity.HEAL_INTERVAL;
            healNearestTwoTeammates();
        }
    }

    private LivingEntity findNearestInjuredTeammate() {
        Level world = drone.level();
        AABB box = drone.getBoundingBox().inflate(5.0); // 20格范围内搜索

        List<LivingEntity> candidates = world.getEntitiesOfClass(LivingEntity.class, box, entity -> {
            if (entity == drone) return false;
            if (entity.isDeadOrDying()) return false;
            if (entity.getHealth() >= entity.getMaxHealth()) return false;

            if (!drone.isAlliedTo(entity)) return false;

            return !drone.isOnlyPlayer() || entity instanceof Player;
        });

        return candidates.stream()
                .min(Comparator.comparingDouble(drone::distanceToSqr))
                .orElse(null);
    }

    private void healNearestTwoTeammates() {
        Level world = drone.level();
        AABB box = drone.getBoundingBox().inflate(20.0);

        List<LivingEntity> injured = world.getEntitiesOfClass(LivingEntity.class, box, entity -> {
            if (entity.isDeadOrDying()) return false;
            if (entity.getHealth() >= entity.getMaxHealth()) return false;
            if (!drone.isAlliedTo(entity)) return false;
            return !drone.isOnlyPlayer() || entity instanceof Player;
        });

        injured.stream()
                .sorted(Comparator.comparingDouble(drone::distanceToSqr))
                .limit(2)
                .forEach(entity -> {
                    entity.heal(entity instanceof Player ? TreatmentDroneEntity.PLAYER_HEAL_AMOUNT : TreatmentDroneEntity.HEAL_AMOUNT);
                    world.broadcastEntityEvent(drone, (byte)7); // 爱心粒子
                    // 添加村民的绿色粒子效果
                    if (world.isClientSide) {
                        for (int i = 0; i < 7; ++i) {
                            double offsetX = this.drone.getRandom().nextGaussian() * 0.02D;
                            double offsetY = this.drone.getRandom().nextGaussian() * 0.02D;
                            double offsetZ = this.drone.getRandom().nextGaussian() * 0.02D;
                            world.addParticle(
                                    ParticleTypes.HAPPY_VILLAGER,
                                    entity.getRandomX(1.0D),
                                    entity.getRandomY() + 0.5D,
                                    entity.getRandomZ(1.0D),
                                    offsetX, offsetY, offsetZ
                            );
                        }
                    }
                });
    }

    @Override
    public void stop() {
        nearestTeammate = null;
        drone.getNavigation().stop();
    }
}
