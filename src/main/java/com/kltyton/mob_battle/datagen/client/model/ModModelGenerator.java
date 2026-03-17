package com.kltyton.mob_battle.datagen.client.model;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ModModelGenerator extends FabricModelProvider {
    public final FabricDataOutput output;

    private static final Model EGG_TEMPLATE = new Model(
            Optional.of(Identifier.of(Mob_battle.MOD_ID, "item/dan")),
            Optional.empty(),
            TextureKey.LAYER0
    );
    private static final Model STICK_TEMPLATE = new Model(
            Optional.of(Identifier.ofVanilla("item/stick")),
            Optional.empty(),
            TextureKey.LAYER0
    );
    public ModModelGenerator(FabricDataOutput output) {
        super(output);
        // 2. 在这里赋值
        this.output = output;
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
        itemModelCollector.register(ModItems.FINE_KNIFE, Models.HANDHELD);

        itemModelCollector.register(ModItems.EMERALD_DIAMOND_HELMET, Models.GENERATED);
        itemModelCollector.register(ModItems.EMERALD_DIAMOND_CHESTPLATE, Models.GENERATED);
        itemModelCollector.register(ModItems.EMERALD_DIAMOND_LEGGINGS, Models.GENERATED);
        itemModelCollector.register(ModItems.EMERALD_DIAMOND_BOOTS, Models.GENERATED);

        itemModelCollector.register(ModItems.EMERALD_DIAMOND_SWORD, Models.HANDHELD);
        itemModelCollector.register(ModItems.ZIJIN_SWORD, Models.HANDHELD);

        itemModelCollector.register(ModItems.CARDIOTONIC_INJECTION, STICK_TEMPLATE);

        itemModelCollector.upload(ModItems.ICE_BOW, Models.BOW);
        itemModelCollector.registerBow(ModItems.ICE_BOW);

        for (Item item : ModItems.GENERATED_ITEMS.values()) {
            itemModelCollector.register(item, Models.GENERATED);
        }

        // 动态生成蛋的模型
        for (SpawnEggItem item : ModItems.SPAWN_EGG_ITEMS.values()) {
            String itemName = Registries.ITEM.getId(item).getPath();
            Identifier textureId;
            Path texturePath = this.output.getModContainer().findPath("assets/" + Mob_battle.MOD_ID + "/textures/item/dan/" + itemName + ".png")
                    .orElse(null);
            if (texturePath != null && Files.exists(texturePath)) {
                textureId = Identifier.of(Mob_battle.MOD_ID, "item/dan/" + itemName);
            } else {
                textureId = Identifier.of(Mob_battle.MOD_ID, "item/dan/dan");
            }
            // 使用模板生成模型，并注入选定的纹理
            itemModelCollector.output.accept(
                    item,
                    ItemModels.basic(
                            EGG_TEMPLATE.upload(
                                    ModelIds.getItemModelId(item),
                                    TextureMap.layer0(textureId),
                                    itemModelCollector.modelCollector
                            )
                    )
            );
        }
    }
}
