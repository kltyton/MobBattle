package com.kltyton.mob_battle.entity.skull.warrior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class SkullWarriorEntitySkill {
    public static void runAttackSkill(SkullWarriorEntity witherSkeletonKingEntity) {
        double range = 0.3D;
        Level world = witherSkeletonKingEntity.level();
        if (witherSkeletonKingEntity.tryAttackBase((ServerLevel)world, witherSkeletonKingEntity.getTarget())) {
            AABB damageBox = witherSkeletonKingEntity.getBoundingBox().inflate(range, range, range);
            world.getEntities(witherSkeletonKingEntity, damageBox).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> entity.isAlliedTo(witherSkeletonKingEntity))
                    .filter(entity -> !entity.isSpectator() && entity.isAlive())
                    .filter(entity -> entity.distanceToSqr(witherSkeletonKingEntity) <= range * range)
                    .forEach(entity -> {
                        if (entity != witherSkeletonKingEntity.getTarget()) {
                            witherSkeletonKingEntity.tryAttackBase((ServerLevel) world, entity);
                        }
                    });
        }
    }
}
