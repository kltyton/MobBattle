package com.kltyton.mob_battle.datagen.client.model;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ModModelGenerator extends FabricModelProvider {
    public final FabricDataOutput output;

    private static final ModelTemplate EGG_TEMPLATE = new ModelTemplate(
            Optional.of(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/dan")),
            Optional.empty(),
            TextureSlot.LAYER0
    );
    private static final ModelTemplate STICK_TEMPLATE = new ModelTemplate(
            Optional.of(ResourceLocation.withDefaultNamespace("item/stick")),
            Optional.empty(),
            TextureSlot.LAYER0
    );
    public ModModelGenerator(FabricDataOutput output) {
        super(output);
        // 2. 在这里赋值
        this.output = output;
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateCollector) {
        blockStateCollector.createNonTemplateModelBlock(ModBlocks.NEST_BLOCK);
        blockStateCollector.createNonTemplateModelBlock(ModBlocks.MUSHROOM_BLOCK);
        registerCompressedBlock(blockStateCollector, ModBlocks.COMPRESSED_IRON_BLOCK);
        registerCompressedBlock(blockStateCollector, ModBlocks.COMPRESSED_GOLD_BLOCK);
        registerCompressedBlock(blockStateCollector, ModBlocks.COMPRESSED_DIAMOND_BLOCK);
        registerCompressedBlock(blockStateCollector, ModBlocks.COMPRESSED_NETHERITE_BLOCK);
        blockStateCollector.createNonTemplateHorizontalBlock(ModBlocks.MACHINE_WORKTABLE_BLOCK);
        blockStateCollector.registerSimpleItemModel(ModBlocks.MACHINE_WORKTABLE_BLOCK, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "block/machine_worktable"));
    }

    private void registerCompressedBlock(BlockModelGenerators blockStateCollector, Block block) {
        String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
        ResourceLocation modelId = ModelTemplates.CUBE_ALL.create(
                block,
                TextureMapping.cube(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "block/compressed/" + blockName)),
                blockStateCollector.modelOutput
        );
        blockStateCollector.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(modelId))
        );
        blockStateCollector.registerSimpleItemModel(block, modelId);
    }


    @Override
    public void generateItemModels(ItemModelGenerators itemModelCollector) {
        itemModelCollector.generateFlatItem(ModBlocks.NEST_BLOCK.asItem(), ModelTemplates.FLAT_ITEM);
        itemModelCollector.generateFlatItem(ModBlocks.MUSHROOM_BLOCK.asItem(), ModelTemplates.FLAT_ITEM);

/*        itemModelCollector.register(ModBlocks.NEST_BLOCK.asItem(),
                new Model(Optional.of(Identifier.of(Mob_battle.MOD_ID, "block/nest_block")), Optional.empty()));
        itemModelCollector.register(ModBlocks.MUSHROOM_BLOCK.asItem(),
                new Model(Optional.of(Identifier.of(Mob_battle.MOD_ID, "block/mushroom_block")), Optional.empty()));*/
        itemModelCollector.generateFlatItem(ModItems.FINE_KNIFE, ModelTemplates.FLAT_HANDHELD_ITEM);

        itemModelCollector.generateFlatItem(ModItems.EMERALD_DIAMOND_HELMET, ModelTemplates.FLAT_ITEM);
        itemModelCollector.generateFlatItem(ModItems.EMERALD_DIAMOND_CHESTPLATE, ModelTemplates.FLAT_ITEM);
        itemModelCollector.generateFlatItem(ModItems.EMERALD_DIAMOND_LEGGINGS, ModelTemplates.FLAT_ITEM);
        itemModelCollector.generateFlatItem(ModItems.EMERALD_DIAMOND_BOOTS, ModelTemplates.FLAT_ITEM);

        itemModelCollector.generateFlatItem(ModItems.EMERALD_DIAMOND_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelCollector.generateFlatItem(ModItems.ZIJIN_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelCollector.generateFlatItem(ModItems.COMPRESSED_IRON_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelCollector.generateFlatItem(ModItems.COMPRESSED_GOLD_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelCollector.generateFlatItem(ModItems.COMPRESSED_DIAMOND_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelCollector.generateFlatItem(ModItems.COMPRESSED_NETHERITE_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);

        itemModelCollector.generateFlatItem(ModItems.CARDIOTONIC_INJECTION, STICK_TEMPLATE);

        itemModelCollector.createFlatItemModel(ModItems.ICE_BOW, ModelTemplates.BOW);
        itemModelCollector.generateBow(ModItems.ICE_BOW);

        for (Item item : ModItems.GENERATED_ITEMS.values()) {
            itemModelCollector.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        }

        // 动态生成蛋的模型
        for (SpawnEggItem item : ModItems.SPAWN_EGG_ITEMS.values()) {
            String itemName = BuiltInRegistries.ITEM.getKey(item).getPath();
            ResourceLocation textureId;
            Path texturePath = this.output.getModContainer().findPath("assets/" + Mob_battle.MOD_ID + "/textures/item/dan/" + itemName + ".png")
                    .orElse(null);
            if (texturePath != null && Files.exists(texturePath)) {
                textureId = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/dan/" + itemName);
            } else {
                textureId = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/dan/dan");
            }
            // 使用模板生成模型，并注入选定的纹理
            itemModelCollector.itemModelOutput.accept(
                    item,
                    ItemModelUtils.plainModel(
                            EGG_TEMPLATE.create(
                                    ModelLocationUtils.getModelLocation(item),
                                    TextureMapping.layer0(textureId),
                                    itemModelCollector.modelOutput
                            )
                    )
            );
        }
    }
}
