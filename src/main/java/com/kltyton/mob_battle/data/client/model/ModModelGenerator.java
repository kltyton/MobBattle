package com.kltyton.mob_battle.data.client.model;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.Models;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModModelGenerator extends FabricModelProvider {

    private static final Model CUSTOM_EGG = new Model(
            Optional.of(Identifier.of(Mob_battle.MOD_ID, "item/dan")),
            Optional.empty()
    );
    public ModModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateCollector) {
        blockStateCollector.registerSimpleState(ModBlocks.NEST_BLOCK);
        blockStateCollector.registerSimpleState(ModBlocks.MUSHROOM_BLOCK);
    }


    @Override
    public void generateItemModels(ItemModelGenerator itemModelCollector) {
        itemModelCollector.register(ModBlocks.NEST_BLOCK.asItem(), Models.GENERATED);
        itemModelCollector.register(ModBlocks.MUSHROOM_BLOCK.asItem(), Models.GENERATED);

/*        itemModelCollector.register(ModBlocks.NEST_BLOCK.asItem(),
                new Model(Optional.of(Identifier.of(Mob_battle.MOD_ID, "block/nest_block")), Optional.empty()));
        itemModelCollector.register(ModBlocks.MUSHROOM_BLOCK.asItem(),
                new Model(Optional.of(Identifier.of(Mob_battle.MOD_ID, "block/mushroom_block")), Optional.empty()));*/
        itemModelCollector.register(ModItems.LOBSTER_MAIN_COURSE, Models.GENERATED);
        itemModelCollector.register(ModItems.COOKED_HIGHBIRD_EGG, Models.GENERATED);
        for (SpawnEggItem item : ModItems.SPAWN_EGG_ITEMS.values()) {
            itemModelCollector.register(item, CUSTOM_EGG);
        }
    }
}
