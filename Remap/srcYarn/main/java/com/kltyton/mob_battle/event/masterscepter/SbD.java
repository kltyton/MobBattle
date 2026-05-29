package com.kltyton.mob_battle.event.masterscepter;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

public class SbD {
    public static void runCommand(ServerPlayerEntity user) {
        double range = 3.0D;
        ServerWorld world = user.getWorld();
        Box damageBox = user.getBoundingBox().expand(range, range, range);
        world.getOtherEntities(user, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !entity.isTeammate(user))
                .filter(entity -> !entity.isSpectator() && entity.isAlive())
                .filter(entity -> entity.squaredDistanceTo(user) <= range * range)
                .forEach(entity -> {
                    if (entity != user) {
                        entity.damage(world, entity.getDamageSources().mobAttack(user), 150F);
                    }
                });
    }
}
