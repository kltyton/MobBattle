package com.kltyton.mob_battle.datagen.server.loot;

import com.kltyton.mob_battle.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableGenerator extends FabricBlockLootTableProvider {
    public ModBlockLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // 简单的掉落自身
        dropSelf(ModBlocks.NEST_BLOCK);
        dropSelf(ModBlocks.MUSHROOM_BLOCK);
        dropSelf(ModBlocks.COMPRESSED_IRON_BLOCK);
        dropSelf(ModBlocks.COMPRESSED_GOLD_BLOCK);
        dropSelf(ModBlocks.COMPRESSED_DIAMOND_BLOCK);
        dropSelf(ModBlocks.COMPRESSED_NETHERITE_BLOCK);
        add(ModBlocks.MACHINE_WORKTABLE_BLOCK, createSinglePropConditionTable(ModBlocks.MACHINE_WORKTABLE_BLOCK, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));

        // 普通挖掘掉其他，精准采集才掉自身：
        // addDrop(ModBlocks.NEST_BLOCK, drops(ModBlocks.NEST_BLOCK, Items.DIRT));
    }
}
