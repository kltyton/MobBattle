package com.kltyton.mob_battle.entity.littleperson.guard.skill;

import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class LittlePersonGuardSkill {
    public static void runSkill_2(LittlePersonGuardEntity littlePersonGuardEntity) {
        List<LivingEntity> livingEntities = getNearbyLivingEntities(littlePersonGuardEntity, 3);
        ServerWorld sw = (ServerWorld) littlePersonGuardEntity.getWorld();
        for (LivingEntity living : livingEntities) {
            living.damage(sw, living.getDamageSources().mobAttack(littlePersonGuardEntity), 90);
        }

    }

    public static List<LivingEntity> getNearbyLivingEntities(LittlePersonGuardEntity entity, double radius) {
        return entity.getWorld().getEntitiesByClass(
                LivingEntity.class,
                entity.getBoundingBox().expand(radius),
                p -> p.isAlive() &&
                        entity.distanceTo(p) <= radius &&
                        p != entity &&
                        p.getScoreboardTeam() != entity.getScoreboardTeam()
        );
    }
}
