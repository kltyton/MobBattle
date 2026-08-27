package com.kltyton.mob_battle.event.golem;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.golem.StrongMinEntity;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.Vec3;

public final class StrongMinVillageSpawnEvent {
    private StrongMinVillageSpawnEvent() {
    }

    public static void init() {
        ServerTickEvents.END_LEVEL_TICK.register(StrongMinVillageSpawnEvent::tickLevel);
    }

    private static void tickLevel(ServerLevel level) {
        if (level.getGameTime() % 200L != 0L) {
            return;
        }
        for (ServerPlayer player : level.players()) {
            if (level.sectionsToVillage(SectionPos.of(player.blockPosition())) > 0) {
                continue;
            }
            boolean hasStrongMin = !level.getEntitiesOfClass(
                    StrongMinEntity.class,
                    player.getBoundingBox().inflate(64.0D),
                    StrongMinEntity::isAlive
            ).isEmpty();
            if (hasStrongMin) {
                continue;
            }
            StrongMinEntity strongMin = ModEntities.STRONG_MIN.create(level, EntitySpawnReason.MOB_SUMMONED);
            if (strongMin == null) {
                continue;
            }
            Vec3 spawnPos = EntityQueries.findSafeSpawnPosition(level, strongMin, player.position(), 12.0D, 3.0D, 40, true)
                    .orElse(player.position());
            strongMin.snapTo(spawnPos.x, spawnPos.y, spawnPos.z, level.getRandom().nextFloat() * 360.0F, 0.0F);
            strongMin.finalizeSpawn(level, level.getCurrentDifficultyAt(strongMin.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
            level.addFreshEntity(strongMin);
            break;
        }
    }
}
