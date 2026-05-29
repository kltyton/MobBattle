package com.kltyton.mob_battle.entity.irongolem.skill;

import com.kltyton.mob_battle.entity.irongolem.VillagerIronGolemEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.AABB;

public class IronGolemSkill {
    public static void runSkill_1_5(VillagerIronGolemEntity villagerIronGolemEntity) {
        Level world = villagerIronGolemEntity.level();
        if (villagerIronGolemEntity.getTarget() != null) {
            villagerIronGolemEntity.tryAttackBase((ServerLevel) world, villagerIronGolemEntity.getTarget(), 1.5f);
        }
    }
    public static void runSkill_2(VillagerIronGolemEntity villagerIronGolemEntity) {
        double range = 5.0D;
        Level world = villagerIronGolemEntity.level();
        if (villagerIronGolemEntity.getTarget() != null && villagerIronGolemEntity.tryAttackBase((ServerLevel) world, villagerIronGolemEntity.getTarget(), 2)) {
            AABB damageBox = villagerIronGolemEntity.getBoundingBox().inflate(range, range, range);
            world.getEntities(villagerIronGolemEntity, damageBox).stream()
                    .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(villagerIronGolemEntity, living))
                    .filter(entity -> entity.distanceToSqr(villagerIronGolemEntity) <= range * range)
                    .forEach(entity -> {
                        if (entity != villagerIronGolemEntity.getTarget()) {
                            villagerIronGolemEntity.tryAttackBase((ServerLevel) world, entity, 2);
                        }
                    });
        }
        world.levelEvent(LevelEvent.PARTICLES_SMASH_ATTACK,
                villagerIronGolemEntity.getOnPos(),
                750);
    }
}
