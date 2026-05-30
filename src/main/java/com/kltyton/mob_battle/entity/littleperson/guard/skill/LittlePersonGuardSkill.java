package com.kltyton.mob_battle.entity.littleperson.guard.skill;

import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class LittlePersonGuardSkill {
    public static void runSkill_2(LittlePersonGuardEntity littlePersonGuardEntity) {
        List<LivingEntity> livingEntities = getNearbyLivingEntities(littlePersonGuardEntity, 3);
        ServerLevel sw = (ServerLevel) littlePersonGuardEntity.level();
        for (LivingEntity living : livingEntities) {
            living.hurtServer(sw, living.damageSources().mobAttack(littlePersonGuardEntity), 90);
        }

    }

    public static List<LivingEntity> getNearbyLivingEntities(LittlePersonGuardEntity entity, double radius) {
        return entity.level().getEntitiesOfClass(
                LivingEntity.class,
                entity.getBoundingBox().inflate(radius),
                p -> p.isAlive() &&
                        entity.distanceTo(p) <= radius &&
                        p != entity &&
                        !p.isAlliedTo(entity)
        );
    }
}
