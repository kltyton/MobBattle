package com.kltyton.mob_battle.data.server.loot;

import com.kltyton.mob_battle.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableGenerator extends FabricBlockLootTableProvider {
    public ModLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // 简单的掉落自身
        addDrop(ModBlocks.NEST_BLOCK);
        addDrop(ModBlocks.MUSHROOM_BLOCK);

        // 普通挖掘掉其他，精准采集才掉自身：
        // addDrop(ModBlocks.NEST_BLOCK, drops(ModBlocks.NEST_BLOCK, Items.DIRT));
    }
}
