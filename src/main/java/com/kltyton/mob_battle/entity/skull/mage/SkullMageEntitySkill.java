package com.kltyton.mob_battle.entity.skull.mage;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.skull.archer.SkullArcherEntity;
import com.kltyton.mob_battle.entity.skull.warrior.SkullWarriorEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SkullMageEntitySkill {
    public static void runAttackSkill(SkullMageEntity skullArcherEntity) {
        if (skullArcherEntity.getTarget() != null) skullArcherEntity.shootAtBase(skullArcherEntity.getTarget(), 1.0F);
    }
    public static void runSummonSkullSkill(SkullMageEntity king) {
        Level world = king.level();
        if (world instanceof ServerLevel serverWorld) {
            for (int i = 0; i < 2; i++) {
                double offsetX = (king.getRandom().nextDouble() - 0.5) * 4.0;
                double offsetZ = (king.getRandom().nextDouble() - 0.5) * 4.0;
                Vec3 summonPos = new Vec3(king.getX() + offsetX, king.getY(), king.getZ() + offsetZ);

                SkullArcherEntity skullEntity = ModEntities.SKULL_ARCHER.create(serverWorld, EntitySpawnReason.MOB_SUMMONED);
                if (skullEntity != null) {
                    Vec3 safeSpawnPos = EntityUtil.findSafeSpawnPosition(serverWorld, skullEntity, summonPos).orElse(summonPos);
                    skullEntity.snapTo(safeSpawnPos.x, safeSpawnPos.y, safeSpawnPos.z, king.getRandom().nextFloat() * 360.0F, 0.0F);
                    if (king.getTarget() != null) {
                        skullEntity.setTarget(king.getTarget());
                    }
                    skullEntity.setOwner(king);
                    EntityUtil.joinSameTeam(skullEntity, king);
                    serverWorld.addFreshEntity(skullEntity);
                }
            }
            for (int i = 0; i < 4; i++) {
                double offsetX = (king.getRandom().nextDouble() - 0.5) * 4.0;
                double offsetZ = (king.getRandom().nextDouble() - 0.5) * 4.0;
                Vec3 summonPos = new Vec3(king.getX() + offsetX, king.getY(), king.getZ() + offsetZ);

                SkullWarriorEntity skullEntity = ModEntities.SKULL_WARRIOR.create(serverWorld, EntitySpawnReason.MOB_SUMMONED);
                if (skullEntity != null) {
                    Vec3 safeSpawnPos = EntityUtil.findSafeSpawnPosition(serverWorld, skullEntity, summonPos).orElse(summonPos);
                    skullEntity.snapTo(safeSpawnPos.x, safeSpawnPos.y, safeSpawnPos.z, king.getRandom().nextFloat() * 360.0F, 0.0F);
                    if (king.getTarget() != null) {
                        skullEntity.setTarget(king.getTarget());
                    }
                    skullEntity.setOwner(king);
                    EntityUtil.joinSameTeam(skullEntity, king);
                    serverWorld.addFreshEntity(skullEntity);
                }
            }
        }
    }
}
