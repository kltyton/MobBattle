package com.kltyton.mob_battle.entity.skull.king;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.skull.archer.SkullArcherEntity;
import com.kltyton.mob_battle.entity.skull.mage.SkullMageEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SkullKingEntitySkill {
    public static void runAttackSkill(SkullKingEntity witherSkeletonKingEntity) {
        double range = 3.0D;
        World world = witherSkeletonKingEntity.getWorld();
        if (witherSkeletonKingEntity.tryAttackBase((ServerWorld)world, witherSkeletonKingEntity.getTarget())) {
            Box damageBox = witherSkeletonKingEntity.getBoundingBox().expand(range, range, range);
            world.getOtherEntities(witherSkeletonKingEntity, damageBox).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> !entity.isTeammate(witherSkeletonKingEntity))
                    .filter(entity -> !entity.isSpectator() && entity.isAlive())
                    .filter(entity -> entity.squaredDistanceTo(witherSkeletonKingEntity) <= range * range)
                    .forEach(entity -> {
                        if (entity != witherSkeletonKingEntity.getTarget()) {
                            witherSkeletonKingEntity.tryAttackBase((ServerWorld) world, entity);
                        }
                    });
        }
    }
    public static void runSuperAttackSkill(SkullKingEntity witherSkeletonKingEntity) {
        double range = 6.0D;
        World world = witherSkeletonKingEntity.getWorld();
        Box damageBox = witherSkeletonKingEntity.getBoundingBox().expand(range, range, range);
        world.getOtherEntities(witherSkeletonKingEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !entity.isTeammate(witherSkeletonKingEntity))
                .filter(entity -> !entity.isSpectator() && entity.isAlive())
                .filter(entity -> entity.squaredDistanceTo(witherSkeletonKingEntity) <= range * range)
                .forEach(entity -> {
                    entity.damage((ServerWorld) world, entity.getDamageSources().mobAttack(witherSkeletonKingEntity), 120F);
                    entity.damage((ServerWorld) world, entity.getDamageSources().magic(), 30F);
                    ((LivingEntity) entity).takeKnockback(2.0D, witherSkeletonKingEntity.getX() - entity.getX(), witherSkeletonKingEntity.getZ() - entity.getZ());
                });

    }
    public static void runSummonSkullSkill(SkullKingEntity king) {
        World world = king.getWorld();
        if (world instanceof ServerWorld serverWorld) {
            // 在国王实体周围召唤两只僵尸
            for (int i = 0; i < 2; i++) {
                // 计算召唤位置，距离国王实体1-3格的随机位置
                double offsetX = (king.getRandom().nextDouble() - 0.5) * 4.0;
                double offsetZ = (king.getRandom().nextDouble() - 0.5) * 4.0;
                Vec3d summonPos = new Vec3d(king.getX() + offsetX, king.getY(), king.getZ() + offsetZ);

                SkullMageEntity skullEntity = ModEntities.SKULL_MAGE.create(serverWorld, SpawnReason.MOB_SUMMONED);
                if (skullEntity != null) {
                    skullEntity.refreshPositionAndAngles(summonPos.x, summonPos.y, summonPos.z, king.getRandom().nextFloat() * 360.0F, 0.0F);
                    if (king.getTarget() != null) {
                        skullEntity.setTarget(king.getTarget());
                    }
                    skullEntity.setOwner(king);
                    serverWorld.spawnEntity(skullEntity);
                }
            }
            for (int i = 0; i < 4; i++) {
                // 计算召唤位置，距离国王实体1-3格的随机位置
                double offsetX = (king.getRandom().nextDouble() - 0.5) * 4.0;
                double offsetZ = (king.getRandom().nextDouble() - 0.5) * 4.0;
                Vec3d summonPos = new Vec3d(king.getX() + offsetX, king.getY(), king.getZ() + offsetZ);

                SkullArcherEntity skullEntity = ModEntities.SKULL_ARCHER.create(serverWorld, SpawnReason.MOB_SUMMONED);
                if (skullEntity != null) {
                    skullEntity.refreshPositionAndAngles(summonPos.x, summonPos.y, summonPos.z, king.getRandom().nextFloat() * 360.0F, 0.0F);
                    if (king.getTarget() != null) {
                        skullEntity.setTarget(king.getTarget());
                    }
                    skullEntity.setOwner(king);
                    serverWorld.spawnEntity(skullEntity);
                }
            }
        }
    }

}
