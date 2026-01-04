package com.kltyton.mob_battle.entity.witherskeletonking.skill;

import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class KingSkill {
    public static void runAttackSkill(WitherSkeletonKingEntity witherSkeletonKingEntity) {
        double range = 3.0D;
        World world = witherSkeletonKingEntity.getWorld();
        if (witherSkeletonKingEntity.tryAttackBase2((ServerWorld)world, witherSkeletonKingEntity.getTarget())) {
            Box damageBox = witherSkeletonKingEntity.getBoundingBox().expand(range, range, range);
            world.getOtherEntities(witherSkeletonKingEntity, damageBox).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> !entity.isTeammate(witherSkeletonKingEntity))
                    .filter(entity -> !entity.isSpectator() && entity.isAlive())
                    .filter(entity -> entity.squaredDistanceTo(witherSkeletonKingEntity) <= range * range)
                    .forEach(entity -> {
                        if (entity != witherSkeletonKingEntity.getTarget()) {
                            witherSkeletonKingEntity.tryAttackBase2((ServerWorld) world, entity);
                        }
                    });
        }
    }
    public static void runSuperAttackSkill(WitherSkeletonKingEntity witherSkeletonKingEntity) {
        double range = 6.0D;
        World world = witherSkeletonKingEntity.getWorld();
        Box damageBox = witherSkeletonKingEntity.getBoundingBox().expand(range, range, range);
        world.getOtherEntities(witherSkeletonKingEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !entity.isTeammate(witherSkeletonKingEntity))
                .filter(entity -> !entity.isSpectator() && entity.isAlive())
                .filter(entity -> entity.squaredDistanceTo(witherSkeletonKingEntity) <= range * range)
                .forEach(entity -> {
                    entity.damage((ServerWorld) world, entity.getDamageSources().mobAttack(witherSkeletonKingEntity), witherSkeletonKingEntity.isHealthy(0.35) ? 280.0F :260.0F);
                    entity.damage((ServerWorld) world, entity.getDamageSources().magic(), witherSkeletonKingEntity.isHealthy(0.35) ? 65.0F : 60.0F);
                    ((LivingEntity) entity).takeKnockback(2.0D, witherSkeletonKingEntity.getX() - entity.getX(), witherSkeletonKingEntity.getZ() - entity.getZ());
                });

    }
    public static void runWitherSkullSkill(WitherSkeletonKingEntity king) {
        LivingEntity target = king.getTarget();
        if (target == null || !target.isAlive()) return;
        World world = king.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return;

        // 播放声音
        world.playSound(null, king.getX(), king.getY(), king.getZ(),
                SoundEvents.ENTITY_WITHER_SHOOT, king.getSoundCategory(),
                3.0F, 1.0F);

        // 计算基础方向
        Vec3d lookDir = target.getEyePos().subtract(king.getEyePos()).normalize();
        // 生成凋零之首
        WitherSkullEntityKing skull = new WitherSkullEntityKing(EntityType.WITHER_SKULL, world, king.isHealthy(0.35) ? 85 : 70);
        skull.setOwner(king);
        skull.setPosition(king.getX() - 0.2, king.getEyeY() - 0.2, king.getZ() - 0.2);

        // 添加轻微偏移（随机散射）
        Vec3d offsetDir = lookDir
                .add(world.getRandom().nextGaussian() * 0.05,
                        world.getRandom().nextGaussian() * 0.05,
                        world.getRandom().nextGaussian() * 0.05)
                .normalize()
                .multiply(1.5); // 初始速度
        skull.setCharged(false);
        skull.setVelocity(offsetDir);
        serverWorld.spawnEntity(skull);

        // ====== 同队增益：力量 VI（6秒）======
        double buffRange = 5.0D;

        Box area = king.getBoundingBox().expand(buffRange);
        world.getOtherEntities(king, area).stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(LivingEntity::isAlive)
                .filter(e -> !e.isSpectator())
                .filter(e -> e.isTeammate(king))
                .forEach(ally -> {
                    ally.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.STRENGTH,
                            200,
                            5,
                            true, true, true));
                    ally.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.FIRE_RESISTANCE,
                            -1,
                            0,
                            true, true, true));
                });
    }
    public static void runWitherAllSkullSkill(WitherSkeletonKingEntity king) {
        World world = king.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return;

        // 播放发射音效
        world.playSound(null, king.getX(), king.getY(), king.getZ(),
                SoundEvents.ENTITY_WITHER_SHOOT, king.getSoundCategory(),
                3.0F, 1.0F);

        // ====== 向随机方向发射多个凋零之首 ======
        int skullCount = king.isHealthy(0.35) ? 54 : 18; // 发射数量
        double speed = 1.6;  // 初始速度

        for (int i = 0; i < skullCount; i++) {
            WitherSkullEntityKing skull = new WitherSkullEntityKing(EntityType.WITHER_SKULL, world, 70);
            skull.setOwner(king);

            // 稍微分散出生点（避免重叠）
            double xOffset = (world.getRandom().nextDouble() - 0.5);
            double yOffset = world.getRandom().nextDouble() * 0.5;
            double zOffset = (world.getRandom().nextDouble() - 0.5);
            skull.setPosition(king.getX() + xOffset, king.getEyeY() + yOffset, king.getZ() + zOffset);

            // 随机方向
            double yaw = world.getRandom().nextDouble() * 2 * Math.PI;
            double pitch = (world.getRandom().nextDouble() - 0.5) * Math.PI / 3.0; // -30°~30°范围内
            double vx = -Math.sin(yaw) * Math.cos(pitch);
            double vy = Math.sin(pitch);
            double vz = Math.cos(yaw) * Math.cos(pitch);

            Vec3d velocity = new Vec3d(vx, vy, vz).normalize().multiply(speed);
            skull.setVelocity(velocity);

            // 偶尔设为带电凋零头
            skull.setCharged(false);

            serverWorld.spawnEntity(skull);
        }

        // ====== 同队增益：力量 VI（6秒）======
        double buffRange = 5.0D;

        Box area = king.getBoundingBox().expand(buffRange);
        world.getOtherEntities(king, area).stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(LivingEntity::isAlive)
                .filter(e -> !e.isSpectator())
                .filter(e -> e.isTeammate(king))
                .forEach(ally -> {
                    ally.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.STRENGTH,
                            200,
                            5,
                            true, true, true));
                    ally.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.FIRE_RESISTANCE,
                            -1,
                            0,
                            true, true, true));
                });
    }

}
