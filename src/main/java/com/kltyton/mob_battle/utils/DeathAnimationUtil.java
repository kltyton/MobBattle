package com.kltyton.mob_battle.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public final class DeathAnimationUtil {
    private DeathAnimationUtil() {
    }

    public static FrozenPose capture(LivingEntity entity) {
        return new FrozenPose(
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                entity.getYRot(),
                entity.getXRot(),
                entity.yHeadRot,
                entity.yBodyRot
        );
    }

    public static FrozenPose captureIfNeeded(LivingEntity entity, FrozenPose pose) {
        return pose == null ? capture(entity) : pose;
    }

    public static void freeze(LivingEntity entity, FrozenPose pose) {
        if (pose == null) {
            return;
        }

        entity.setPos(pose.x(), pose.y(), pose.z());
        entity.setDeltaMovement(Vec3.ZERO);
        entity.hurtMarked = true;

        entity.setYRot(pose.yRot());
        entity.setXRot(pose.xRot());
        entity.setYHeadRot(pose.yHeadRot());
        entity.setYBodyRot(pose.yBodyRot());
        entity.yRotO = pose.yRot();
        entity.xRotO = pose.xRot();
        entity.yHeadRotO = pose.yHeadRot();
        entity.yBodyRotO = pose.yBodyRot();

        if (entity instanceof Mob mob) {
            mob.setTarget(null);
            mob.setAggressive(false);
            mob.getNavigation().stop();
            mob.setNoAi(true);
        }
    }

    public record FrozenPose(
            double x,
            double y,
            double z,
            float yRot,
            float xRot,
            float yHeadRot,
            float yBodyRot
    ) {
    }
}
