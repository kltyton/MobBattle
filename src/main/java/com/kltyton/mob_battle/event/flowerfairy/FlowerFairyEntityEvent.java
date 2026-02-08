package com.kltyton.mob_battle.event.flowerfairy;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.flowerfairy.FlowerFairyEntity;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;

public class FlowerFairyEntityEvent {
    public static void init() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient && state.isOf(Blocks.WILDFLOWERS)) {
                if (world.random.nextFloat() < 0.07f) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    FlowerFairyEntity fairy = ModEntities.FLOWER_FAIRY.create(serverWorld, SpawnReason.EVENT);
                    if (fairy != null) {
                        fairy.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                        serverWorld.spawnEntity(fairy);
                    }
                }
            }
        });
    }
}
