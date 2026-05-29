package com.kltyton.mob_battle.entity.witherskeletonking.skill;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.enhancedwither.EnhancedWitherEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.WitherSkeletonDogEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.summon.DualBladeWitherSkeletonEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.summon.ShieldAxeWitherSkeletonEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WitherSkeletonKingEntitySkill {
    public static void runAttackSkill(WitherSkeletonKingEntity witherSkeletonKingEntity) {
        double range = 3.0D;
        Level world = witherSkeletonKingEntity.level();
        if (witherSkeletonKingEntity.tryAttackBase2((ServerLevel)world, witherSkeletonKingEntity.getTarget())) {
            AABB damageBox = witherSkeletonKingEntity.getBoundingBox().inflate(range, range, range);
            world.getEntities(witherSkeletonKingEntity, damageBox).stream()
                    .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(witherSkeletonKingEntity, living))
                    .filter(entity -> entity.distanceToSqr(witherSkeletonKingEntity) <= range * range)
                    .forEach(entity -> {
                        if (entity != witherSkeletonKingEntity.getTarget()) {
                            witherSkeletonKingEntity.tryAttackBase2((ServerLevel) world, entity);
                        }
                    });
        }
    }
    public static void runSuperAttackSkill(WitherSkeletonKingEntity witherSkeletonKingEntity) {
        double range = 6.0D;
        Level world = witherSkeletonKingEntity.level();
        AABB damageBox = witherSkeletonKingEntity.getBoundingBox().inflate(range, range, range);
        world.getEntities(witherSkeletonKingEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(witherSkeletonKingEntity, living))
                .filter(entity -> entity.distanceToSqr(witherSkeletonKingEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 260.0f;
                    float magicDamage = 60.0f;
                    if (witherSkeletonKingEntity.isHealthy(0.75)) attackDamage = 300.0f;
                    if (witherSkeletonKingEntity.isHealthy(0.35)) {
                        attackDamage = 350.0f;
                        magicDamage = 75.0f;
                    }
                    entity.hurtServer((ServerLevel) world, entity.damageSources().mobAttack(witherSkeletonKingEntity), attackDamage);
                    entity.hurtServer((ServerLevel) world, entity.damageSources().magic(), magicDamage);
                    if (entity instanceof LivingEntity living) {
                        applyDecay(living, witherSkeletonKingEntity);
                    }
                    ((LivingEntity) entity).knockback(2.0D, witherSkeletonKingEntity.getX() - entity.getX(), witherSkeletonKingEntity.getZ() - entity.getZ());
                });

    }
    public static void runWitherSkullSkill(WitherSkeletonKingEntity king) {
        LivingEntity target = king.getTarget();
        if (target == null || !EntityUtil.isValidCombatTarget(king, target)) return;
        Level world = king.level();
        if (!(world instanceof ServerLevel serverWorld)) return;

        // 播放声音
        world.playSound(null, king.getX(), king.getY(), king.getZ(),
                SoundEvents.WITHER_SHOOT, king.getSoundSource(),
                3.0F, 1.0F);

        // 计算基础方向
        Vec3 lookDir = target.getEyePosition().subtract(king.getEyePosition()).normalize();
        // 生成凋零之首
        WitherSkullKingEntity skull = new WitherSkullKingEntity(EntityType.WITHER_SKULL, world, king.isHealthy(0.35) ? 85 : 70);
        skull.setOwner(king);
        skull.setPos(king.getX() - 0.2, king.getEyeY() - 0.2, king.getZ() - 0.2);

        // 添加轻微偏移（随机散射）
        Vec3 offsetDir = lookDir
                .add(world.getRandom().nextGaussian() * 0.05,
                        world.getRandom().nextGaussian() * 0.05,
                        world.getRandom().nextGaussian() * 0.05)
                .normalize()
                .scale(1.5); // 初始速度
        skull.setDangerous(false);
        skull.setDeltaMovement(offsetDir);
        serverWorld.addFreshEntity(skull);

        // ====== 同队增益：力量 VI（6秒）======
        double buffRange = 5.0D;

        AABB area = king.getBoundingBox().inflate(buffRange);
        world.getEntities(king, area).stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(e -> e.isAlive() && !EntityUtil.isCreativeOrSpectator(e))
                .filter(e -> e.isAlliedTo(king))
                .forEach(ally -> {
                    ally.addEffect(new MobEffectInstance(
                            MobEffects.STRENGTH,
                            200,
                            5,
                            true, true, true));
                    ally.addEffect(new MobEffectInstance(
                            MobEffects.FIRE_RESISTANCE,
                            -1,
                            0,
                            true, true, true));
                });
    }
    public static void runSuperWitherSkullSkill(WitherSkeletonKingEntity king) {
        Level world = king.level();
        // 播放发射音效
        world.playSound(null, king.getX(), king.getY(), king.getZ(),
                SoundEvents.WITHER_SHOOT, king.getSoundSource(),
                3.0F, 1.0F);
        // ====== 向随机方向发射多个凋零之首 ======
        int skullCount = 5;
        RandomSource random = world.getRandom();
        for (int i = 0; i < skullCount; i++) {
            // 稍微分散出生点（避免重叠）
            // 1. 扩大出生点分布范围 (左右 1.5 格, 上下 2.0 格)
            double xOffset = (random.nextDouble() - 0.5) * 8.0;
            double yOffset = (random.nextDouble() - 0.5) * 8.0; // 围绕眼睛上下浮动
            double zOffset = (random.nextDouble() - 0.5) * 8.0;

            Vec3 lookDir = king.getViewVector(1.0F);
            Vec3 velocity = lookDir.add(
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
            bullet.setPos(king.getX() + xOffset, king.getEyeY() + yOffset, king.getZ() + zOffset);
            world.addFreshEntity(bullet);
        }
    }
    public static void runWitherAllSkullSkill(WitherSkeletonKingEntity king) {
        Level world = king.level();
        if (!(world instanceof ServerLevel serverWorld)) return;

        // 播放发射音效
        world.playSound(null, king.getX(), king.getY(), king.getZ(),
                SoundEvents.WITHER_SHOOT, king.getSoundSource(),
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
            skull.setPos(king.getX() + xOffset, king.getEyeY() + yOffset, king.getZ() + zOffset);

            // 随机方向
            double yaw = world.getRandom().nextDouble() * 2 * Math.PI;
            double pitch = (world.getRandom().nextDouble() - 0.5) * Math.PI / 3.0; // -30°~30°范围内
            double vx = -Math.sin(yaw) * Math.cos(pitch);
            double vy = Math.sin(pitch);
            double vz = Math.cos(yaw) * Math.cos(pitch);

            Vec3 velocity = new Vec3(vx, vy, vz).normalize().scale(speed);
            skull.setDeltaMovement(velocity);

            // 偶尔设为带电凋零头
            skull.setDangerous(false);

            serverWorld.addFreshEntity(skull);
        }

        // ====== 同队增益：力量 VI（6秒）======
        double buffRange = 5.0D;

        AABB area = king.getBoundingBox().inflate(buffRange);
        world.getEntities(king, area).stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(e -> e.isAlive() && !EntityUtil.isCreativeOrSpectator(e))
                .filter(e -> e.isAlliedTo(king))
                .forEach(ally -> {
                    ally.addEffect(new MobEffectInstance(
                            MobEffects.STRENGTH,
                            200,
                            5,
                            true, true, true));
                    ally.addEffect(new MobEffectInstance(
                            MobEffects.FIRE_RESISTANCE,
                            -1,
                            0,
                            true, true, true));
                });
    }

    public static void runThornSkill(WitherSkeletonKingEntity king) {
        LivingEntity target = king.getTarget();
        if (target == null || !EntityUtil.isValidCombatTarget(king, target) || !(king.level() instanceof ServerLevel world)) {
            return;
        }
        Vec3 direction = target.position().subtract(king.position());
        Vec3 horizontal = new Vec3(direction.x, 0.0D, direction.z);
        Vec3 pos = target.position();
        if (horizontal.lengthSqr() > 1.0E-4D) {
            pos = target.position().subtract(horizontal.normalize().scale(0.75D));
        }
        king.teleportTo(pos.x, target.getY(), pos.z);
        king.lookAt(target, 30.0F, 30.0F);
        target.hurtServer(world, king.damageSources().mobAttack(king), 220.0F);
        target.hurtServer(world, king.damageSources().indirectMagic(king, king), 70.0F);
        applyDecay(target, king);
    }

    public static void runEnhanceWitherCallSkill(WitherSkeletonKingEntity king) {
        if (!(king.level() instanceof ServerLevel world)) {
            return;
        }
        spawnKingSummon(king, ModEntities.ENHANCED_WITHER.create(world, EntitySpawnReason.MOB_SUMMONED), world, 0);
        spawnKingSummon(king, ModEntities.DUAL_BLADE_WITHER_SKELETON.create(world, EntitySpawnReason.MOB_SUMMONED), world, 1);
        spawnKingSummon(king, ModEntities.SHIELD_AXE_WITHER_SKELETON.create(world, EntitySpawnReason.MOB_SUMMONED), world, 2);
    }

    public static void spawnWitherSkeletonDogs(WitherSkeletonKingEntity king) {
        if (!(king.level() instanceof ServerLevel world)) {
            return;
        }
        spawnKingSummon(king, ModEntities.WITHER_SKELETON_DOG.create(world, EntitySpawnReason.MOB_SUMMONED), world, 0, 2);
        spawnKingSummon(king, ModEntities.WITHER_SKELETON_DOG.create(world, EntitySpawnReason.MOB_SUMMONED), world, 1, 2);
    }

    private static void spawnKingSummon(WitherSkeletonKingEntity king, Entity summon, ServerLevel world, int index) {
        spawnKingSummon(king, summon, world, index, 3);
    }

    private static void spawnKingSummon(WitherSkeletonKingEntity king, Entity summon, ServerLevel world, int index, int count) {
        if (summon == null) {
            return;
        }
        double angle = Math.PI * 2.0D * index / count;
        Vec3 desiredPos = king.position().add(Math.cos(angle) * 3.0D, 1.0D, Math.sin(angle) * 3.0D);
        Vec3 pos = EntityUtil.findSafeSpawnPosition(world, summon, desiredPos).orElse(desiredPos);
        summon.snapTo(pos.x, pos.y, pos.z, king.getYRot(), 0.0F);
        EntityUtil.joinSameTeam(summon, king);
        if (summon instanceof EnhancedWitherEntity enhancedWither) {
            enhancedWither.setSummonOwner(king);
        } else if (summon instanceof DualBladeWitherSkeletonEntity dualBlade) {
            dualBlade.setSummonOwner(king);
        } else if (summon instanceof ShieldAxeWitherSkeletonEntity shieldAxe) {
            shieldAxe.setSummonOwner(king);
        } else if (summon instanceof WitherSkeletonDogEntity dog) {
            dog.setSummonOwner(king);
        }
        if (summon instanceof Mob mob) {
            mob.setTarget(king.getTarget());
        }
        world.addFreshEntity(summon);
    }

    private static void applyDecay(LivingEntity target, Entity source) {
        target.addEffect(new MobEffectInstance(ModEffects.DECAY_ENTRY, 3 * 20, 0), source);
    }

}
