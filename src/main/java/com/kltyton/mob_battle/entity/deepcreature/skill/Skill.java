package com.kltyton.mob_battle.entity.deepcreature.skill;

import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.kltyton.mob_battle.utils.TaskSchedulerUtil;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class Skill {
    public static void runRoarSkill(DeepCreatureEntity entity) {
        double radius = 10.0D;
        List<LivingEntity> players = SkillUtils.getNearbyPlayers(entity, radius);
        if (players.isEmpty()) return;

        ServerLevel sw = (ServerLevel) entity.level();
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
        //抓取攻击额外伤害，暂不启用
/*        if (entity.getGrabTargetId() != -1) {
            ServerWorld sw = (ServerWorld) entity.getWorld();
            LivingEntity player = (LivingEntity) entity.getWorld().getEntityById(entity.getGrabTargetId());
            if (player != null) {
                player.damage(sw, player.getDamageSources().magic(), 60);
            }
        }*/
    }
    public static void stopRunCatch(DeepCreatureEntity entity) {
        if (entity.getGrabTargetId() == -1) {
            entity.stopTriggeredAnim("skill_controller", "catch");
        }
    }
    public static void runDamage(DeepCreatureEntity entity) {
        if (entity.getTarget() == null) return;
        entity.getTarget().hurtServer((ServerLevel) entity.level(), entity.getTarget().damageSources().mobAttack(entity), 5);
        TaskSchedulerUtil.runLater(15, () -> {
            entity.getTarget().hurtServer((ServerLevel) entity.level(), entity.getTarget().damageSources().mobAttack(entity), 5);
        });
        TaskSchedulerUtil.runLater(25, () -> {
            entity.getTarget().hurtServer((ServerLevel) entity.level(), entity.getTarget().damageSources().mobAttack(entity), 5);
        });
    }
    public static void runCatchEnd(DeepCreatureEntity entity) {
        if (entity.getGrabTargetId() == -1) return;
        entity.setGrabTargetId(-1);
    }
    public static void runSmashGround(DeepCreatureEntity entity, double radius, double horizPower, double vertPowerBase, double vertPowerRand, double spacing, double delayPerRing) {
        List<LivingEntity> players = SkillUtils.getNearbyPlayers(entity, radius);
        if (players.isEmpty()) return;
        ServerLevel sw = (ServerLevel) entity.level();
        for (LivingEntity player : players) {
            SkillUtils.knockbackPlayer(entity, player, horizPower, vertPowerBase, vertPowerRand);
        }
        SkillUtils.spawnParticles(sw, entity, 60, radius);
        SkillUtils.spawnEvokerFangsRing(sw, entity, radius, spacing, delayPerRing);
    }
    public static void runSmash(DeepCreatureEntity entity) {
        double radius = 8.0D;
        if (entity.getTarget() == null || entity.distanceTo(entity.getTarget()) > radius) return;
        ServerLevel sw = (ServerLevel) entity.level();
        entity.getTarget().hurtServer(sw, entity.getTarget().damageSources().mobAttack(entity), 85F);
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

        ServerLevel sw = (ServerLevel) entity.level();
        for (LivingEntity player : players) {
            SkillUtils.knockbackPlayer(entity, player, 0.8, 0.2, 0.05);
            player.hurtServer(sw, player.damageSources().mobAttack(entity), 90F);
        }
    }
    public static void runSonicBoom(DeepCreatureEntity entity) {
        if (!(entity.level() instanceof ServerLevel world)) return;
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
        double dy = (target.getY() + target.getEyeHeight() * 0.5)
                - (entity.getY() + entity.getEyeHeight() * 0.9);
        double yaw = (float)(Math.toDegrees(Math.atan2(-dx, dz)));
        double pitch = (float)(-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))));
        entity.setYRot((float) yaw);
        entity.setXRot((float) pitch);
        entity.yHeadRot = entity.getYRot();

        // === 起点、终点 ===
        Vec3 headPos = entity.position().add(0, entity.getEyeHeight() * 0.9, 0);
        // 计算“往前偏移一点”的方向向量
        Vec3 lookDir = entity.getViewVector(1.0F).normalize();
        Vec3 start = headPos.add(lookDir.scale(forwardOffset)).add(0, verticalOffset, 0);

        Vec3 end = target.position().add(0, target.getEyeHeight() * 0.5, 0);
        Vec3 delta = end.subtract(start);
        double distance = delta.length();
        if (distance < 1e-3) return;

        Vec3 direction = delta.normalize();

        // === 声音 ===
        entity.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);

        // === 粒子路径 ===
        int steps = (int) (distance / step);
        for (int i = 0; i <= steps; i++) {
            Vec3 pos = start.add(direction.scale(i * step));
            world.sendParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, particleDensity, 0, 0, 0, 0);
        }

        // === 命中检测 ===
        List<LivingEntity> players = world.getEntitiesOfClass(LivingEntity.class,
                entity.getBoundingBox().inflate(distance + 16),
                p -> EntityUtil.isValidCombatTarget(entity, p));

        for (LivingEntity player : players) {
            Vec3 playerPos = player.position().add(0, player.getEyeHeight() * 0.5, 0);
            Vec3 toPlayer = playerPos.subtract(start);

            double proj = toPlayer.dot(direction);
            if (proj < 0 || proj > distance) continue;

            Vec3 closestPoint = start.add(direction.scale(proj));
            double distFromLine = playerPos.distanceTo(closestPoint);

            if (distFromLine <= hitRadius) {
                if (player.hurtServer(world, world.damageSources().magic(), (float) damage)) {
                    player.push(direction.x * knockbackH,
                            direction.y * knockbackV,
                            direction.z * knockbackH);
                    player.hurtMarked = true;
                }
            }
        }
    }
    public static void runCharge(DeepCreatureEntity entity) {
        if (entity.getTarget() == null) return;
        entity.setNoAi(false);
        // 1. 算方向 —— 只算一次
        Vec3 dir = entity.getTarget().position()
                .subtract(entity.position())
                .normalize();
        entity.chargeDir = dir;
        entity.chargeTicksLeft = 40;
    }
}
