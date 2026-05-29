package com.kltyton.mob_battle.entity.skull.warrior;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class SkullWarriorEntitySkill {
    public static void runAttackSkill(SkullWarriorEntity witherSkeletonKingEntity) {
        double range = 0.3D;
        World world = witherSkeletonKingEntity.getWorld();
        if (witherSkeletonKingEntity.tryAttackBase((ServerWorld)world, witherSkeletonKingEntity.getTarget())) {
            Box damageBox = witherSkeletonKingEntity.getBoundingBox().expand(range, range, range);
            world.getOtherEntities(witherSkeletonKingEntity, damageBox).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> entity.isTeammate(witherSkeletonKingEntity))
                    .filter(entity -> !entity.isSpectator() && entity.isAlive())
                    .filter(entity -> entity.squaredDistanceTo(witherSkeletonKingEntity) <= range * range)
                    .forEach(entity -> {
                        if (entity != witherSkeletonKingEntity.getTarget()) {
                            witherSkeletonKingEntity.tryAttackBase((ServerWorld) world, entity);
                        }
                    });
        }
    }
}
