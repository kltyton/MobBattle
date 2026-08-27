package com.kltyton.mob_battle.datagen.server.loot;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
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
        // scarecrow/target 为双格方块,与 machine_worktable 一致:仅下半部分掉落自身,
        // 保持旧手写 loot table 的 lower-half 语义不变。
        add(ModBlocks.SCARECROW_BLOCK, createSinglePropConditionTable(
                ModBlocks.SCARECROW_BLOCK,
                BlockStateProperties.DOUBLE_BLOCK_HALF,
                DoubleBlockHalf.LOWER
        ).setRandomSequence(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "blocks/scarecrow")));
        add(ModBlocks.TARGET_BLOCK, createSinglePropConditionTable(
                ModBlocks.TARGET_BLOCK,
                BlockStateProperties.DOUBLE_BLOCK_HALF,
                DoubleBlockHalf.LOWER
        ).setRandomSequence(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "blocks/target")));

        // 普通挖掘掉其他，精准采集才掉自身：
        // addDrop(ModBlocks.NEST_BLOCK, drops(ModBlocks.NEST_BLOCK, Items.DIRT));
    }
}
