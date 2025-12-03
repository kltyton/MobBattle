package com.kltyton.mob_battle.entity.deepcreature.skill;

import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Skill {
    public static void runRoarSkill(DeepCreatureEntity entity) {
        double radius = 10.0D;
        List<LivingEntity> players = SkillUtils.getNearbyPlayers(entity, radius);
        if (players.isEmpty()) return;

        ServerWorld sw = (ServerWorld) entity.getWorld();
        for (LivingEntity player : players) {
            SkillUtils.knockbackPlayer(entity, player, 1.5, 0.95, 0.2);
        }
        SkillUtils.spawnParticles(sw, entity, 40, 24);
    }

    public static void runEarthquake(DeepCreatureEntity entity) {
        runSmashGround(entity, 24.0D, 0.5, 2.0, 0.2, 2.0 , 0.5);
    }
    public static void runCatch(DeepCreatureEntity entity) {
        double radius = 8.0D;
        if (entity.getTarget() == null || entity.distanceTo(entity.getTarget()) > radius) {
            return;
        }
        entity.setGrabTargetId(entity.getTarget().getId());
    }
    public static void runCatchDamage(DeepCreatureEntity entity) {
        if (entity.getGrabTargetId() != -1) {
            ServerWorld sw = (ServerWorld) entity.getWorld();
            LivingEntity player = (LivingEntity) entity.getWorld().getEntityById(entity.getGrabTargetId());
            if (player != null) {
                player.damage(sw, player.getDamageSources().magic(), 150);
            }
        }
    }
    public static void stopRunCatch(DeepCreatureEntity entity) {
        if (entity.getGrabTargetId() == -1) {
            entity.stopTriggeredAnim("skill_controller", "catch");
        }
    }
    public static void runDamage(DeepCreatureEntity entity) {
        if (entity.getTarget() == null) return;
        entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getTarget().getDamageSources().mobAttack(entity), 5);
        TaskSchedulerUtil.runLater(15, () -> {
            entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getTarget().getDamageSources().mobAttack(entity), 5);
        });
        TaskSchedulerUtil.runLater(25, () -> {
            entity.getTarget().damage((ServerWorld) entity.getWorld(), entity.getTarget().getDamageSources().mobAttack(entity), 5);
        });
    }
    public static void runCatchEnd(DeepCreatureEntity entity) {
        if (entity.getGrabTargetId() == -1) return;
        entity.setGrabTargetId(-1);
    }
    public static void runSmashGround(DeepCreatureEntity entity, double radius, double horizPower, double vertPowerBase, double vertPowerRand, double spacing, double delayPerRing) {
        List<LivingEntity> players = SkillUtils.getNearbyPlayers(entity, radius);
        if (players.isEmpty()) return;
        ServerWorld sw = (ServerWorld) entity.getWorld();
        for (LivingEntity player : players) {
            SkillUtils.knockbackPlayer(entity, player, horizPower, vertPowerBase, vertPowerRand);
        }
        SkillUtils.spawnParticles(sw, entity, 60, radius);
        SkillUtils.spawnEvokerFangsRing(sw, entity, radius, spacing, delayPerRing);
    }
    public static void runSmash(DeepCreatureEntity entity) {
        double radius = 8.0D;
        if (entity.getTarget() == null || entity.distanceTo(entity.getTarget()) > radius) return;
        ServerWorld sw = (ServerWorld) entity.getWorld();
        entity.getTarget().damage(sw, entity.getTarget().getDamageSources().mobAttack(entity), 85F);
/*        sw.createExplosion(
                entity,
                entity.getDamageSources().mobAttack(entity),
                null,
                entity.getTarget().getX(),
                entity.getTarget().getY(),
                entity.getTarget().getZ(),
                1.0F,
                false,
                ServerWorld.ExplosionSourceType.NONE
        );*/
    }

    public static void runSideSkill(DeepCreatureEntity entity) {
        double radius = 12.0D;
        List<LivingEntity> players = SkillUtils.getNearbyPlayers(entity, radius);
        if (players.isEmpty()) return;

        ServerWorld sw = (ServerWorld) entity.getWorld();
        for (LivingEntity player : players) {
            SkillUtils.knockbackPlayer(entity, player, 0.8, 0.2, 0.05);
            player.damage(sw, player.getDamageSources().mobAttack(entity), 90F);
        }
    }
    public static void runSonicBoom(DeepCreatureEntity entity) {
        if (!(entity.getWorld() instanceof ServerWorld world)) return;
        if (entity.getTarget() == null) return;

        var target = entity.getTarget();

        // === 参数 ===
        double damage = 120.0;
        double knockbackH = 2.5;
        double knockbackV = 0.5;
        double hitRadius = 2.0; // 声波宽度
        double step = 0.5;       // 粒子步长
        double forwardOffset = 1.5; // 从头部往前偏移的距离
        double verticalOffset = 0.2; // 头部再稍微往上偏一点
        int particleDensity = 1;

        // === 让实体先面向目标 ===
        double dx = target.getX() - entity.getX();
        double dz = target.getZ() - entity.getZ();
        double dy = (target.getY() + target.getStandingEyeHeight() * 0.5)
                - (entity.getY() + entity.getStandingEyeHeight() * 0.9);
        double yaw = (float)(Math.toDegrees(Math.atan2(-dx, dz)));
        double pitch = (float)(-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))));
        entity.setYaw((float) yaw);
        entity.setPitch((float) pitch);
        entity.headYaw = entity.getYaw();

        // === 起点、终点 ===
        Vec3d headPos = entity.getPos().add(0, entity.getStandingEyeHeight() * 0.9, 0);
        // 计算“往前偏移一点”的方向向量
        Vec3d lookDir = entity.getRotationVec(1.0F).normalize();
        Vec3d start = headPos.add(lookDir.multiply(forwardOffset)).add(0, verticalOffset, 0);

        Vec3d end = target.getPos().add(0, target.getStandingEyeHeight() * 0.5, 0);
        Vec3d delta = end.subtract(start);
        double distance = delta.length();
        if (distance < 1e-3) return;

        Vec3d direction = delta.normalize();

        // === 声音 ===
        entity.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 3.0F, 1.0F);

        // === 粒子路径 ===
        int steps = (int) (distance / step);
        for (int i = 0; i <= steps; i++) {
            Vec3d pos = start.add(direction.multiply(i * step));
            world.spawnParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, particleDensity, 0, 0, 0, 0);
        }

        // === 命中检测 ===
        List<LivingEntity> players = world.getEntitiesByClass(LivingEntity.class,
                entity.getBoundingBox().expand(distance + 4),
                p -> {
            if (p instanceof PlayerEntity player) {
                if (!player.isCreative()) return false;
            }
            return p.isAlive() && !p.isSpectator();
        });

        for (LivingEntity player : players) {
            Vec3d playerPos = player.getPos().add(0, player.getStandingEyeHeight() * 0.5, 0);
            Vec3d toPlayer = playerPos.subtract(start);

            double proj = toPlayer.dotProduct(direction);
            if (proj < 0 || proj > distance) continue;

            Vec3d closestPoint = start.add(direction.multiply(proj));
            double distFromLine = playerPos.distanceTo(closestPoint);

            if (distFromLine <= hitRadius) {
                if (player.damage(world, world.getDamageSources().magic(), (float) damage)) {
                    player.addVelocity(direction.x * knockbackH,
                            direction.y * knockbackV,
                            direction.z * knockbackH);
                    player.velocityModified = true;
                }
            }
        }
    }
    public static void runCharge(DeepCreatureEntity entity) {
        if (entity.getTarget() == null) return;
        entity.setAiDisabled(false);
        // 1. 算方向 —— 只算一次
        Vec3d dir = entity.getTarget().getPos()
                .subtract(entity.getPos())
                .normalize();
        entity.chargeDir = dir;
        entity.chargeTicksLeft = 40;
    }
}
