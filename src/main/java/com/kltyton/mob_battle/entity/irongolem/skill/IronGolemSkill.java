package com.kltyton.mob_battle.entity.irongolem.skill;

import com.kltyton.mob_battle.entity.irongolem.VillagerIronGolemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class IronGolemSkill {
    public static void runSkill_1_5(VillagerIronGolemEntity villagerIronGolemEntity) {
        World world = villagerIronGolemEntity.getWorld();
        if (villagerIronGolemEntity.getTarget() != null) {
            villagerIronGolemEntity.tryAttackBase((ServerWorld) world, villagerIronGolemEntity.getTarget(), 1.5f);
        }
    }
    public static void runSkill_2(VillagerIronGolemEntity villagerIronGolemEntity) {
        double range = 5.0D;
        World world = villagerIronGolemEntity.getWorld();
        if (villagerIronGolemEntity.getTarget() != null && villagerIronGolemEntity.tryAttackBase((ServerWorld) world, villagerIronGolemEntity.getTarget(), 2)) {
            Box damageBox = villagerIronGolemEntity.getBoundingBox().expand(range, range, range);
            world.getOtherEntities(villagerIronGolemEntity, damageBox).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> entity.getScoreboardTeam() != villagerIronGolemEntity.getScoreboardTeam())
                    .filter(entity -> !entity.isSpectator() && entity.isAlive())
                    .filter(entity -> entity.squaredDistanceTo(villagerIronGolemEntity) <= range * range)
                    .forEach(entity -> {
                        if (entity != villagerIronGolemEntity.getTarget()) {
                            villagerIronGolemEntity.tryAttackBase((ServerWorld) world, entity, 2);
                        }
                    });
        }
        world.syncWorldEvent(WorldEvents.SMASH_ATTACK,
                villagerIronGolemEntity.getSteppingPos(),
                750);
    }
}
