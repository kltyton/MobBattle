package com.kltyton.mob_battle.entity.skull.mage;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.skull.archer.SkullArcherEntity;
import com.kltyton.mob_battle.entity.skull.warrior.SkullWarriorEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SkullMageEntitySkill {
    public static void runAttackSkill(SkullMageEntity skullArcherEntity) {
        if (skullArcherEntity.getTarget() != null) skullArcherEntity.shootAtBase(skullArcherEntity.getTarget(), 1.0F);
    }
    public static void runSummonSkullSkill(SkullMageEntity king) {
        World world = king.getWorld();
        if (world instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 2; i++) {
                double offsetX = (king.getRandom().nextDouble() - 0.5) * 4.0;
                double offsetZ = (king.getRandom().nextDouble() - 0.5) * 4.0;
                Vec3d summonPos = new Vec3d(king.getX() + offsetX, king.getY(), king.getZ() + offsetZ);

                SkullArcherEntity skullEntity = ModEntities.SKULL_ARCHER.create(serverWorld, SpawnReason.MOB_SUMMONED);
                if (skullEntity != null) {
                    Vec3d safeSpawnPos = EntityUtil.findSafeSpawnPosition(serverWorld, skullEntity, summonPos).orElse(summonPos);
                    skullEntity.refreshPositionAndAngles(safeSpawnPos.x, safeSpawnPos.y, safeSpawnPos.z, king.getRandom().nextFloat() * 360.0F, 0.0F);
                    if (king.getTarget() != null) {
                        skullEntity.setTarget(king.getTarget());
                    }
                    skullEntity.setOwner(king);
                    serverWorld.spawnEntity(skullEntity);
                }
            }
            for (int i = 0; i < 4; i++) {
                double offsetX = (king.getRandom().nextDouble() - 0.5) * 4.0;
                double offsetZ = (king.getRandom().nextDouble() - 0.5) * 4.0;
                Vec3d summonPos = new Vec3d(king.getX() + offsetX, king.getY(), king.getZ() + offsetZ);

                SkullWarriorEntity skullEntity = ModEntities.SKULL_WARRIOR.create(serverWorld, SpawnReason.MOB_SUMMONED);
                if (skullEntity != null) {
                    Vec3d safeSpawnPos = EntityUtil.findSafeSpawnPosition(serverWorld, skullEntity, summonPos).orElse(summonPos);
                    skullEntity.refreshPositionAndAngles(safeSpawnPos.x, safeSpawnPos.y, safeSpawnPos.z, king.getRandom().nextFloat() * 360.0F, 0.0F);
                    if (king.getTarget() != null) {
                        skullEntity.setTarget(king.getTarget());
                    }
                    skullEntity.setOwner(king);
                    serverWorld.spawnEntity(skullEntity);
                }
            }
        }
    }
}
