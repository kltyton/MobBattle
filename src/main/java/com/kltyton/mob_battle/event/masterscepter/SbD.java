package com.kltyton.mob_battle.event.masterscepter;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public class SbD {
    public static void runCommand(ServerPlayer user) {
        double range = 3.0D;
        ServerLevel world = user.level();
        AABB damageBox = user.getBoundingBox().inflate(range, range, range);
        world.getEntities(user, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !entity.isAlliedTo(user))
                .filter(entity -> !entity.isSpectator() && entity.isAlive())
                .filter(entity -> entity.distanceToSqr(user) <= range * range)
                .forEach(entity -> {
                    if (entity != user) {
                        entity.hurtServer(world, entity.damageSources().mobAttack(user), 150F);
                    }
                });
    }
}
