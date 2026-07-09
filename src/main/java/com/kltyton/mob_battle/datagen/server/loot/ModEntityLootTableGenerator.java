package com.kltyton.mob_battle.datagen.server.loot;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import java.util.concurrent.CompletableFuture;

public class ModEntityLootTableGenerator extends FabricEntityLootSubProvider {
    public ModEntityLootTableGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generate() {
        this.add(
                ModEntities.LOBSTER,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(ModItems.LOBSTER))
                )
        );
        this.add(
                ModEntities.MAGMA_LOBSTER,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(ModItems.MAGMA_LOBSTER))
                )
        );
        this.add(
                ModEntities.LITTLE_PERSON_KING,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(ModItems.LITTLE_PERSON_SCEPTER))
                )
        );
        this.add(
                ModEntities.BLOOD_MAN,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(ModItems.BLOOD_KNIFE))
                )
        );
        this.add(
                ModEntities.POISONOUS_SLASH,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(ModItems.POISON_KNIFE))
                )
        );
        this.add(
                ModEntities.IRON_MAN_TRUE,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(ModItems.IRON_MAN_MISSILE_LAUNCHER))
                )
        );
        this.add(
                ModEntities.LITTLE_PERSON_WORKER,
                LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(ModItems.LITTLE_PERSON_TOOL)
                                        .when(LootItemKilledByPlayerCondition.killedByPlayer()))
                )
        );
    }
}
