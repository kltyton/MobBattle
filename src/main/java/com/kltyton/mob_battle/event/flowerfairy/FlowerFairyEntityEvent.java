package com.kltyton.mob_battle.event.flowerfairy;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.flowerfairy.FlowerFairyEntity;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.Blocks;

public class FlowerFairyEntityEvent {
    public static void init() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClientSide && state.is(Blocks.WILDFLOWERS)) {
                if (world.random.nextFloat() < 0.07f) {
                    ServerLevel serverWorld = (ServerLevel) world;
                    FlowerFairyEntity fairy = ModEntities.FLOWER_FAIRY.create(serverWorld, EntitySpawnReason.EVENT);
                    if (fairy != null) {
                        fairy.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                        serverWorld.addFreshEntity(fairy);
                    }
                }
            }
        });
    }
}
