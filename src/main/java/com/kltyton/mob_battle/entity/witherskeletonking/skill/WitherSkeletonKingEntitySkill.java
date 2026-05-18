package com.kltyton.mob_battle.entity.witherskeletonking.skill;

import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class WitherSkeletonKingEntitySkill {
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
                    float attackDamage = 260.0f;
                    float magicDamage = 60.0f;
                    if (witherSkeletonKingEntity.isHealthy(0.75)) attackDamage = 300.0f;
                    if (witherSkeletonKingEntity.isHealthy(0.35)) {
                        attackDamage = 350.0f;
                        magicDamage = 75.0f;
                    }
                    entity.damage((ServerWorld) world, entity.getDamageSources().mobAttack(witherSkeletonKingEntity), attackDamage);
                    entity.damage((ServerWorld) world, entity.getDamageSources().magic(), magicDamage);
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
        WitherSkullKingEntity skull = new WitherSkullKingEntity(EntityType.WITHER_SKULL, world, king.isHealthy(0.35) ? 85 : 70);
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
    public static void runSuperWitherSkullSkill(WitherSkeletonKingEntity king) {
        World world = king.getWorld();
        // 播放发射音效
        world.playSound(null, king.getX(), king.getY(), king.getZ(),
                SoundEvents.ENTITY_WITHER_SHOOT, king.getSoundCategory(),
                3.0F, 1.0F);
        // ====== 向随机方向发射多个凋零之首 ======
        int skullCount = 5;
        Random random = world.getRandom();
        for (int i = 0; i < skullCount; i++) {
            // 稍微分散出生点（避免重叠）
            // 1. 扩大出生点分布范围 (左右 1.5 格, 上下 2.0 格)
            double xOffset = (random.nextDouble() - 0.5) * 8.0;
            double yOffset = (random.nextDouble() - 0.5) * 8.0; // 围绕眼睛上下浮动
            double zOffset = (random.nextDouble() - 0.5) * 8.0;

            Vec3d lookDir = king.getRotationVec(1.0F);
            Vec3d velocity = lookDir.add(
                    (random.nextDouble() - 0.5) * 8.0, // X轴扰动
                    (random.nextDouble() - 0.5) * 8.0, // Y轴扰动
                    (random.nextDouble() - 0.5) * 8.0  // Z轴扰动
            ).normalize(); // 重新归一化，确保速度一致

            // 3. 确定主轴逻辑 (保留你原有的 Axis 逻辑)
            Direction.Axis mainAxis = Math.abs(velocity.x) > Math.abs(velocity.z) ?
                    Direction.Axis.X : Direction.Axis.Z;

            WitherSkullBulletEntity bullet = new WitherSkullBulletEntity(
                    world,
                    king,
                    king.getTarget(),
                    mainAxis);
            // 把子弹加到世界
            bullet.setPosition(king.getX() + xOffset, king.getEyeY() + yOffset, king.getZ() + zOffset);
            world.spawnEntity(bullet);
        }
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
            WitherSkullKingEntity skull = new WitherSkullKingEntity(EntityType.WITHER_SKULL, world, 70);
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
