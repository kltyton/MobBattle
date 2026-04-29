package com.kltyton.mob_battle.datagen.server.loot;

import com.kltyton.mob_battle.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableGenerator extends FabricBlockLootTableProvider {
    public ModBlockLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // 简单的掉落自身
        addDrop(ModBlocks.NEST_BLOCK);
        addDrop(ModBlocks.MUSHROOM_BLOCK);
        addDrop(ModBlocks.COMPRESSED_IRON_BLOCK);
        addDrop(ModBlocks.COMPRESSED_GOLD_BLOCK);
        addDrop(ModBlocks.COMPRESSED_DIAMOND_BLOCK);
        addDrop(ModBlocks.COMPRESSED_NETHERITE_BLOCK);
        addDrop(ModBlocks.MACHINE_WORKTABLE_BLOCK, dropsWithProperty(ModBlocks.MACHINE_WORKTABLE_BLOCK, Properties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));

        // 普通挖掘掉其他，精准采集才掉自身：
        // addDrop(ModBlocks.NEST_BLOCK, drops(ModBlocks.NEST_BLOCK, Items.DIRT));
    }
}
