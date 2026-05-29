package com.kltyton.mob_battle.entity.skull.king;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.skull.IModSkullEntity;
import com.kltyton.mob_battle.entity.skull.archer.SkullArcherEntity;
import com.kltyton.mob_battle.entity.skull.mage.SkullMageEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SkullKingEntitySkill {
    public static void runAttackSkill(SkullKingEntity witherSkeletonKingEntity) {
        double range = 3.0D;
        Level world = witherSkeletonKingEntity.level();
        if (witherSkeletonKingEntity.tryAttackBase((ServerLevel)world, witherSkeletonKingEntity.getTarget())) {
            AABB damageBox = witherSkeletonKingEntity.getBoundingBox().inflate(range, range, range);
            world.getEntities(witherSkeletonKingEntity, damageBox).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> !entity.isAlliedTo(witherSkeletonKingEntity))
                    .filter(entity -> !entity.isSpectator() && entity.isAlive())
                    .filter(entity -> entity.distanceToSqr(witherSkeletonKingEntity) <= range * range)
                    .forEach(entity -> {
                        boolean isFriend = false;
                        if (entity instanceof IModSkullEntity skullEntity) {
                            isFriend = skullEntity.isOwner(witherSkeletonKingEntity);
                        }
                        if (entity != witherSkeletonKingEntity.getTarget() && !isFriend) {
                            witherSkeletonKingEntity.tryAttackBase((ServerLevel) world, entity);
                        }
                    });
        }
    }
    public static void runSuperAttackSkill(SkullKingEntity witherSkeletonKingEntity) {
        double range = 6.0D;
        Level world = witherSkeletonKingEntity.level();
        AABB damageBox = witherSkeletonKingEntity.getBoundingBox().inflate(range, range, range);
        world.getEntities(witherSkeletonKingEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !entity.isAlliedTo(witherSkeletonKingEntity))
                .filter(entity -> !entity.isSpectator() && entity.isAlive())
                .filter(entity -> entity.distanceToSqr(witherSkeletonKingEntity) <= range * range)
                .forEach(entity -> {
                    boolean isFriend = false;
                    if (entity instanceof IModSkullEntity skullEntity) {
                        isFriend = skullEntity.isOwner(witherSkeletonKingEntity);
                    }
                    if (!isFriend) {
                        entity.hurtServer((ServerLevel) world, entity.damageSources().mobAttack(witherSkeletonKingEntity), 120F);
                        entity.hurtServer((ServerLevel) world, entity.damageSources().magic(), 30F);
                        ((LivingEntity) entity).knockback(2.0D, witherSkeletonKingEntity.getX() - entity.getX(), witherSkeletonKingEntity.getZ() - entity.getZ());
                    }
                });
    }
    public static void runSummonSkullSkill(SkullKingEntity king) {
        Level world = king.level();
        if (world instanceof ServerLevel serverWorld) {
            // 在国王实体周围召唤两只僵尸
            for (int i = 0; i < 2; i++) {
                // 计算召唤位置，距离国王实体1-3格的随机位置
                double offsetX = (king.getRandom().nextDouble() - 0.5) * 4.0;
                double offsetZ = (king.getRandom().nextDouble() - 0.5) * 4.0;
                Vec3 summonPos = new Vec3(king.getX() + offsetX, king.getY(), king.getZ() + offsetZ);

                SkullMageEntity skullEntity = ModEntities.SKULL_MAGE.create(serverWorld, EntitySpawnReason.MOB_SUMMONED);
                if (skullEntity != null) {
                    Vec3 safeSpawnPos = EntityUtil.findSafeSpawnPosition(serverWorld, skullEntity, summonPos).orElse(summonPos);
                    skullEntity.snapTo(safeSpawnPos.x, safeSpawnPos.y, safeSpawnPos.z, king.getRandom().nextFloat() * 360.0F, 0.0F);
                    if (king.getTarget() != null) {
                        skullEntity.setTarget(king.getTarget());
                    }
                    skullEntity.setOwner(king);
                    EntityUtil.joinSameTeam(skullEntity, king);
                    serverWorld.addFreshEntity(skullEntity);
                }
            }
            for (int i = 0; i < 4; i++) {
                // 计算召唤位置，距离国王实体1-3格的随机位置
                double offsetX = (king.getRandom().nextDouble() - 0.5) * 4.0;
                double offsetZ = (king.getRandom().nextDouble() - 0.5) * 4.0;
                Vec3 summonPos = new Vec3(king.getX() + offsetX, king.getY(), king.getZ() + offsetZ);

                SkullArcherEntity skullEntity = ModEntities.SKULL_ARCHER.create(serverWorld, EntitySpawnReason.MOB_SUMMONED);
                if (skullEntity != null) {
                    Vec3 safeSpawnPos = EntityUtil.findSafeSpawnPosition(serverWorld, skullEntity, summonPos).orElse(summonPos);
                    skullEntity.snapTo(safeSpawnPos.x, safeSpawnPos.y, safeSpawnPos.z, king.getRandom().nextFloat() * 360.0F, 0.0F);
                    if (king.getTarget() != null) {
                        skullEntity.setTarget(king.getTarget());
                    }
                    skullEntity.setOwner(king);
                    EntityUtil.joinSameTeam(skullEntity, king);
                    serverWorld.addFreshEntity(skullEntity);
                }
            }
        }
    }

}
