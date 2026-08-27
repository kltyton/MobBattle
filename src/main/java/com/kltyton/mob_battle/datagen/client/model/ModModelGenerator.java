package com.kltyton.mob_battle.datagen.client.model;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ModModelGenerator extends FabricModelProvider {
    public final FabricPackOutput output;

    private static final ModelTemplate EGG_TEMPLATE = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/dan")),
            Optional.empty(),
            TextureSlot.LAYER0
    );
    private static final ModelTemplate STICK_TEMPLATE = new ModelTemplate(
            Optional.of(Identifier.withDefaultNamespace("item/stick")),
            Optional.empty(),
            TextureSlot.LAYER0
    );
    public ModModelGenerator(FabricPackOutput output) {
        super(output);
        this.output = output;
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateCollector) {
        blockStateCollector.createNonTemplateModelBlock(ModBlocks.NEST_BLOCK);
        blockStateCollector.createNonTemplateHorizontalBlock(ModBlocks.MUSHROOM_BLOCK);
        registerCompressedBlock(blockStateCollector, ModBlocks.COMPRESSED_IRON_BLOCK);
        registerCompressedBlock(blockStateCollector, ModBlocks.COMPRESSED_GOLD_BLOCK);
        registerCompressedBlock(blockStateCollector, ModBlocks.COMPRESSED_DIAMOND_BLOCK);
        registerCompressedBlock(blockStateCollector, ModBlocks.COMPRESSED_NETHERITE_BLOCK);
        // 双格结构方块:模型正面方向必须与旧手写 blockstate 一致(scarecrow=NORTH、target=EAST)。
        FacingBlockModels.register(blockStateCollector, ModBlocks.SCARECROW_BLOCK, Direction.NORTH);
        FacingBlockModels.register(blockStateCollector, ModBlocks.TARGET_BLOCK, Direction.EAST);
        blockStateCollector.createNonTemplateHorizontalBlock(ModBlocks.MACHINE_WORKTABLE_BLOCK);
        blockStateCollector.registerSimpleItemModel(ModBlocks.MACHINE_WORKTABLE_BLOCK, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "block/machine_worktable"));
    }

    private void registerCompressedBlock(BlockModelGenerators blockStateCollector, Block block) {
        String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
        Identifier modelId = ModelTemplates.CUBE_ALL.create(
                block,
                TextureMapping.cube(new Material(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "block/compressed/" + blockName))),
                blockStateCollector.modelOutput
        );
        blockStateCollector.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(modelId))
        );
        blockStateCollector.registerSimpleItemModel(block, modelId);
    }


    @Override
    public void generateItemModels(ItemModelGenerators itemModelCollector) {
        itemModelCollector.itemModelOutput.accept(ModBlocks.NEST_BLOCK.asItem(), ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "block/nest")));
        itemModelCollector.itemModelOutput.accept(ModBlocks.MUSHROOM_BLOCK.asItem(), ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "block/mushroom")));
        itemModelCollector.generateFlatItem(ModItems.FINE_KNIFE, ModelTemplates.FLAT_HANDHELD_ITEM);

        itemModelCollector.generateFlatItem(ModItems.EMERALD_DIAMOND_HELMET, ModelTemplates.FLAT_ITEM);
        itemModelCollector.generateFlatItem(ModItems.EMERALD_DIAMOND_CHESTPLATE, ModelTemplates.FLAT_ITEM);
        itemModelCollector.generateFlatItem(ModItems.EMERALD_DIAMOND_LEGGINGS, ModelTemplates.FLAT_ITEM);
        itemModelCollector.generateFlatItem(ModItems.EMERALD_DIAMOND_BOOTS, ModelTemplates.FLAT_ITEM);

        itemModelCollector.generateFlatItem(ModItems.EMERALD_DIAMOND_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelCollector.generateFlatItem(ModItems.ZIJIN_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        generateItemWithTexture(itemModelCollector, ModItems.COMPRESSED_COPPER_SWORD,
                Identifier.withDefaultNamespace("item/copper_sword"), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelCollector.generateFlatItem(ModItems.COMPRESSED_IRON_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelCollector.generateFlatItem(ModItems.COMPRESSED_GOLD_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelCollector.generateFlatItem(ModItems.COMPRESSED_DIAMOND_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelCollector.generateFlatItem(ModItems.COMPRESSED_NETHERITE_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        generateFlatItemWithTexture(itemModelCollector, ModItems.COMPRESSED_COPPER_HELMET, Identifier.withDefaultNamespace("item/copper_helmet"));
        generateFlatItemWithTexture(itemModelCollector, ModItems.COMPRESSED_COPPER_CHESTPLATE, Identifier.withDefaultNamespace("item/copper_chestplate"));
        generateFlatItemWithTexture(itemModelCollector, ModItems.COMPRESSED_COPPER_LEGGINGS, Identifier.withDefaultNamespace("item/copper_leggings"));
        generateFlatItemWithTexture(itemModelCollector, ModItems.COMPRESSED_COPPER_BOOTS, Identifier.withDefaultNamespace("item/copper_boots"));

        itemModelCollector.generateFlatItem(ModItems.CARDIOTONIC_INJECTION, STICK_TEMPLATE);
        registerExistingItemModel(itemModelCollector, ModItems.POISON_KNIFE, "poison_knife");
        registerExistingItemModel(itemModelCollector, ModItems.BLOOD_KNIFE, "blood_knife");
        registerExistingItemModel(itemModelCollector, ModItems.IRON_MAN_MISSILE_LAUNCHER, "iron_man_missile_launcher");
        registerExistingItemModel(itemModelCollector, ModItems.LITTLE_PERSON_TOOL, "little_person_tool");
        registerExistingItemModel(itemModelCollector, ModItems.LITTLE_PERSON_SCEPTER, "little_person_scepter");

        itemModelCollector.createFlatItemModel(ModItems.ICE_BOW, ModelTemplates.BOW);
        itemModelCollector.generateBow(ModItems.ICE_BOW);

        for (Item item : ModItems.GENERATED_ITEMS.values()) {
            if (isCompressedCopperArmor(item)) {
                continue;
            } else if (item == ModItems.PURIFICATION_SCROLL) {
                generateFlatItemWithTexture(itemModelCollector, item,
                        Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/slowness_scroll"));
            } else if (item == ModItems.PIGLIN_CANNON) {
                generateFlatItemWithTexture(itemModelCollector, item,
                        Identifier.withDefaultNamespace("item/crossbow_standby"));
            } else if (item == ModItems.WOODEN_WHISTLE) {
                generateFlatItemWithTexture(itemModelCollector, item,
                        Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/wooden_whistle"));
            } else if (item == ModItems.BIG_BACKPACK) {
                generateFlatItemWithTexture(itemModelCollector, item,
                        Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/big_backpack"));
            } else if (item == ModItems.ENDER_PURPLE_PEARL) {
                generateFlatItemWithTexture(itemModelCollector, item,
                        Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/ender_purple_pearl"));
            } else if (item == ModItems.ICE_SWORD) {
                generateItemWithTexture(itemModelCollector, item,
                        Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/ice_sword"), ModelTemplates.FLAT_HANDHELD_ITEM);
            } else if (item == ModItems.FIRE_SWORD) {
                generateItemWithTexture(itemModelCollector, item,
                        Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/fire_sword"), ModelTemplates.FLAT_HANDHELD_ITEM);
            } else if (item == ModItems.CHASING_WIND_SWORD) {
                generateItemWithTexture(itemModelCollector, item,
                        Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/chasing_wind_sword"), ModelTemplates.FLAT_HANDHELD_ITEM);
            } else {
                itemModelCollector.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
            }
        }

        // 动态生成蛋的模型
        for (SpawnEggItem item : ModItems.SPAWN_EGG_ITEMS.values()) {
            String itemName = BuiltInRegistries.ITEM.getKey(item).getPath();
            Identifier textureId;
            if (item == ModItems.PIGLIN_BRUTE_SPEAR_USE_SPAWN_EGG
                    || item == ModItems.PIGLIN_BRUTE_SPEAR_MELEE_SPAWN_EGG
                    || item == ModItems.PIGLIN_BRUTE_SPEAR_MOD_SPAWN_EGG
                    || item == ModItems.PIGLIN_BRUTE_BOW_SPAWN_EGG
                    || item == ModItems.PIGLIN_BRUTE_CROSSBOW_SPAWN_EGG) {
                textureId = Identifier.withDefaultNamespace("item/piglin_brute_spawn_egg");
            } else if (item == ModItems.BOW_ZOMBIE_SPAWN_EGG
                    || item == ModItems.BOW_ZOMBIE_MOD_SPAWN_EGG) {
                textureId = Identifier.withDefaultNamespace("item/zombie_spawn_egg");
            } else {
                Path texturePath = this.output.getModContainer().findPath("assets/" + Mob_battle.MOD_ID + "/textures/item/dan/" + itemName + ".png")
                        .orElse(null);
                if (texturePath != null && Files.exists(texturePath)) {
                    textureId = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/dan/" + itemName);
                } else {
                    textureId = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/dan/dan");
                }
            }
            // 使用模板生成模型，并注入选定的纹理
            itemModelCollector.itemModelOutput.accept(
                    item,
                    ItemModelUtils.plainModel(
                            EGG_TEMPLATE.create(
                                    ModelLocationUtils.getModelLocation(item),
                                    TextureMapping.layer0(new Material(textureId)),
                                    itemModelCollector.modelOutput
                            )
                    )
            );
        }

        // 旧手写 items/*.json 描述符的代码生成:标准 flat 模型同时生成模型本体,
        // 复杂/自定义 display 模型只生成 item descriptor(模型本体保留手写)。
        generateHandwrittenItemModels(itemModelCollector);
    }

    /**
     * 依据 HandwrittenItemModels 目录生成旧手写物品描述符。
     * 标准 flat 模型生成模型本体与描述符;复杂/自定义模型仅生成描述符,
     * 不覆写手写模型本体,保证 head display 等 API 无法表达的语义不丢失。
     */
    private static void generateHandwrittenItemModels(ItemModelGenerators itemModelCollector) {
        for (HandwrittenItemModels.BodySpec spec : HandwrittenItemModels.standardFlatBodySpecs()) {
            spec.template().create(spec.modelLocation(), spec.textures(), itemModelCollector.modelOutput);
            itemModelCollector.itemModelOutput.accept(spec.item(), ItemModelUtils.plainModel(spec.modelLocation()));
        }
        for (HandwrittenItemModels.DescriptorSpec spec : HandwrittenItemModels.descriptorOnlySpecs()) {
            itemModelCollector.itemModelOutput.accept(spec.item(), ItemModelUtils.plainModel(spec.modelLocation()));
        }
    }

    private static boolean isCompressedCopperArmor(Item item) {
        return item == ModItems.COMPRESSED_COPPER_HELMET
                || item == ModItems.COMPRESSED_COPPER_CHESTPLATE
                || item == ModItems.COMPRESSED_COPPER_LEGGINGS
                || item == ModItems.COMPRESSED_COPPER_BOOTS;
    }

    private static void generateFlatItemWithTexture(ItemModelGenerators itemModelCollector, Item item, Identifier textureId) {
        generateItemWithTexture(itemModelCollector, item, textureId, ModelTemplates.FLAT_ITEM);
    }

    private static void generateItemWithTexture(ItemModelGenerators itemModelCollector, Item item, Identifier textureId, ModelTemplate template) {
        itemModelCollector.itemModelOutput.accept(
                item,
                ItemModelUtils.plainModel(
                        template.create(
                                ModelLocationUtils.getModelLocation(item),
                                TextureMapping.layer0(new Material(textureId)),
                                itemModelCollector.modelOutput
                        )
                )
        );
    }

    private static void registerExistingItemModel(ItemModelGenerators itemModelCollector, Item item, String modelPath) {
        itemModelCollector.itemModelOutput.accept(
                item,
                ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/" + modelPath))
        );
    }
}
