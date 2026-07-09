package com.kltyton.mob_battle.datagen.server.loot;

import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableGenerator extends FabricBlockLootSubProvider {
    public ModBlockLootTableGenerator(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
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
        add(Blocks.COBBLESTONE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Blocks.COBBLESTONE)))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(LootItemRandomChanceCondition.randomChance(0.02F))
                        .add(LootItem.lootTableItem(ModItems.LITTLE_STONE))));
        add(ModBlocks.MACHINE_WORKTABLE_BLOCK, createSinglePropConditionTable(ModBlocks.MACHINE_WORKTABLE_BLOCK, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));

        // 普通挖掘掉其他，精准采集才掉自身：
        // addDrop(ModBlocks.NEST_BLOCK, drops(ModBlocks.NEST_BLOCK, Items.DIRT));
    }
}
