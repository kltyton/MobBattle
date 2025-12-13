package com.kltyton.mob_battle.entity.drone.goal;

import com.kltyton.mob_battle.entity.drone.treatmentdrone.TreatmentDroneEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class HealTeamGoal extends Goal {

    private final TreatmentDroneEntity drone;
    private final double speed;
    private LivingEntity nearestTeammate;

    public HealTeamGoal(TreatmentDroneEntity drone, double speed) {
        this.drone = drone;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        // 没主人或没驯服就不治疗
        if (!drone.isTamed() || drone.getOwner() == null) {
            return false;
        }

        // 找到最近的需要治疗的队友
        nearestTeammate = findNearestInjuredTeammate();
        return nearestTeammate != null;
    }

    @Override
    public boolean shouldContinue() {
        return nearestTeammate != null && nearestTeammate.isAlive() && drone.squaredDistanceTo(nearestTeammate) > 2.0;
    }

    @Override
    public void start() {
        drone.getNavigation().startMovingTo(nearestTeammate, speed);
    }

    @Override
    public void tick() {
        if (nearestTeammate == null || !nearestTeammate.isAlive()) {
            return;
        }

        double distSq = drone.squaredDistanceTo(nearestTeammate);

        // 保持一个舒适的治疗距离（5~10格）
        if (distSq > 9.0) {
            drone.getNavigation().startMovingTo(nearestTeammate, speed);
        } else if (distSq < 2.0) {
            drone.getNavigation().stop();
        } else {
            drone.getNavigation().stop();
            drone.getLookControl().lookAt(nearestTeammate, 30.0F, 30.0F);
        }

        if (--drone.healTickTimer <= 0) {
            drone.healTickTimer = TreatmentDroneEntity.HEAL_INTERVAL;
            healNearestTwoTeammates();
        }
    }

    private LivingEntity findNearestInjuredTeammate() {
        World world = drone.getWorld();
        Box box = drone.getBoundingBox().expand(5.0); // 20格范围内搜索

        List<LivingEntity> candidates = world.getEntitiesByClass(LivingEntity.class, box, entity -> {
            if (entity == drone) return false;
            if (entity.isDead()) return false;
            if (entity.getHealth() >= entity.getMaxHealth()) return false;

            if (!drone.isTeammate(entity)) return false;

            return !drone.isOnlyPlayer() || entity instanceof PlayerEntity;
        });

        return candidates.stream()
                .min(Comparator.comparingDouble(drone::squaredDistanceTo))
                .orElse(null);
    }

    private void healNearestTwoTeammates() {
        World world = drone.getWorld();
        Box box = drone.getBoundingBox().expand(20.0);

        List<LivingEntity> injured = world.getEntitiesByClass(LivingEntity.class, box, entity -> {
            if (entity.isDead()) return false;
            if (entity.getHealth() >= entity.getMaxHealth()) return false;
            if (!drone.isTeammate(entity)) return false;
            return !drone.isOnlyPlayer() || entity instanceof PlayerEntity;
        });

        injured.stream()
                .sorted(Comparator.comparingDouble(drone::squaredDistanceTo))
                .limit(2)
                .forEach(entity -> {
                    entity.heal(drone.isOnlyPlayer() ? TreatmentDroneEntity.PLAYER_HEAL_AMOUNT : TreatmentDroneEntity.HEAL_AMOUNT);
                    world.sendEntityStatus(drone, (byte)7); // 爱心粒子
                    // 添加村民的绿色粒子效果
                    if (world.isClient) {
                        for (int i = 0; i < 7; ++i) {
                            double offsetX = this.drone.getRandom().nextGaussian() * 0.02D;
                            double offsetY = this.drone.getRandom().nextGaussian() * 0.02D;
                            double offsetZ = this.drone.getRandom().nextGaussian() * 0.02D;
                            world.addParticleClient(
                                    ParticleTypes.HAPPY_VILLAGER,
                                    entity.getParticleX(1.0D),
                                    entity.getRandomBodyY() + 0.5D,
                                    entity.getParticleZ(1.0D),
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
