package com.kltyton.mob_battle.datagen.client.model;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.registry.BaseMaterialItems;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import java.util.List;

/**
 * 旧手写物品模型目录。
 *
 * <p>描述 {@code src/main/resources/assets/mob_battle/items} 与
 * {@code src/main/resources/assets/mob_battle/models/item} 中仍由手写 JSON 维护的
 * 物品描述符与模型本体。ModModelGenerator 依据本目录生成等价 JSON,使手写文件在
 * Datagen 语义等价验证后可删除;复杂 Blockbench 模型本体与自定义 display 仍保留手写。</p>
 *
 * <p>兼容边界:本目录只收录 26.1.2 注册表中真实存在的物品。meteoricore_hoe、
 * meteoricore_pickaxe、meteoricore_shovel、meteoricore_fishing_rod 等未注册物品,
 * 以及 vs_snipe / meteoricore_bow 的复杂分发描述符没有 Item 实例或属于复杂分发,
 * 不在生成范围内,继续保留手写。</p>
 */
public final class HandwrittenItemModels {

    /** 描述符与模型本体均可由 Datagen 生成的物品。 */
    public record BodySpec(Item item, Identifier modelLocation, ModelTemplate template, TextureMapping textures) {
    }

    /** 仅生成 item descriptor、模型本体保留手写的物品。 */
    public record DescriptorSpec(Item item, Identifier modelLocation) {
    }

    private HandwrittenItemModels() {
    }

    private static Identifier itemModel(String path) {
        return Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "item/" + path);
    }

    private static Material itemTexture(String path) {
        return new Material(itemModel(path));
    }

    /** 双层 flat 模型:父模型 item/generated,层 0 与层 1 使用同一纹理(旧护甲手写语义)。 */
    private static BodySpec twoLayerArmor(Item item, String modelPath) {
        Material texture = itemTexture(modelPath);
        return new BodySpec(
                item,
                itemModel(modelPath),
                ModelTemplates.TWO_LAYERED_ITEM,
                TextureMapping.layered(texture, texture)
        );
    }

    /** 手持 flat 模型:父模型 item/handheld,层 0 使用指定纹理路径。 */
    private static BodySpec handheldItem(Item item, String modelPath, String texturePath) {
        return new BodySpec(
                item,
                itemModel(modelPath),
                ModelTemplates.FLAT_HANDHELD_ITEM,
                TextureMapping.layer0(itemTexture(texturePath))
        );
    }

    private static DescriptorSpec descriptor(Item item, String path) {
        // 调用方传入的是完整的模型资源路径（例如 item/blue_ice）。这里不能再次
        // 拼接 item/，否则会生成 mob_battle:item/item/blue_ice 这类不可加载路径。
        return new DescriptorSpec(item, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, path));
    }

    private static DescriptorSpec assetDescriptor(Item item, String path) {
        return new DescriptorSpec(item, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, path));
    }

    /**
     * 标准 flat 模型(无自定义 display):同时生成模型本体与 item descriptor,
     * 生成结果与手写 JSON 语义等价,手写两处文件均可删除。
     */
    public static List<BodySpec> standardFlatBodySpecs() {
        return List.of(
                twoLayerArmor(ModItems.ECREDCULTIST_BOOTS, "armor/ecredcultist_boots"),
                twoLayerArmor(ModItems.ECREDCULTIST_CHESTPLATE, "armor/ecredcultist_chest"),
                twoLayerArmor(ModItems.ECREDCULTIST_HELMET, "armor/ecredcultist_helmet"),
                twoLayerArmor(ModItems.ECREDCULTIST_LEGGINGS, "armor/ecredcultist_leggings"),
                twoLayerArmor(ModItems.HELL_HELMET_1, "armor/hell_helmet_1"),
                twoLayerArmor(ModItems.HELL_CHESTPLATE_1, "armor/hell_chestplate_1"),
                twoLayerArmor(ModItems.HELL_LEGGINGS_1, "armor/hell_leggings_1"),
                twoLayerArmor(ModItems.HELL_BOOTS_1, "armor/hell_boots_1"),
                twoLayerArmor(ModItems.HELL_HELMET_2, "armor/hell_helmet_2"),
                twoLayerArmor(ModItems.HELL_CHESTPLATE_2, "armor/hell_chestplate_2"),
                twoLayerArmor(ModItems.HELL_LEGGINGS_2, "armor/hell_leggings_2"),
                twoLayerArmor(ModItems.HELL_BOOTS_2, "armor/hell_boots_2"),
                twoLayerArmor(ModItems.IRON_GOLD_HELMET, "armor/iron_gold_helmet"),
                twoLayerArmor(ModItems.IRON_GOLD_CHESTPLATE, "armor/iron_gold_chestplate"),
                twoLayerArmor(ModItems.IRON_GOLD_LEGGINGS, "armor/iron_gold_leggings"),
                twoLayerArmor(ModItems.IRON_GOLD_BOOTS, "armor/iron_gold_boots"),
                handheldItem(ModItems.IRON_GOLD_SWORD, "iron_gold_items/iron_gold_sword", "iron_gold/iron_gold_sword")
        );
    }

    /**
     * 仅生成 item descriptor 的物品:
     * 1) 父模型 item/generated 但带自定义 head display 的旧材料/卷轴,display 无法用
     *    ModelTemplate 表达,模型本体必须保留手写;
     * 2) 复杂 Blockbench 模型本体(incubation_egg、meteoricore_axe),只补描述符。
     */
    public static List<DescriptorSpec> descriptorOnlySpecs() {
        return List.of(
                descriptor(ModItems.BIG_FIREBALL_SCROLL, "item/big_fireball_scroll"),
                descriptor(BaseMaterialItems.ITEMS.get("blue_ice"), "item/blue_ice"),
                descriptor(BaseMaterialItems.ITEMS.get("desert_crystal"), "item/desert_crystal"),
                descriptor(BaseMaterialItems.ITEMS.get("emerald_diamond"), "item/emerald_diamond"),
                descriptor(BaseMaterialItems.ITEMS.get("energy"), "item/energy"),
                descriptor(BaseMaterialItems.ITEMS.get("fire_crystal"), "item/fire_crystal"),
                descriptor(BaseMaterialItems.ITEMS.get("fire_red"), "item/fire_red"),
                descriptor(ModItems.FIRE_WALL_SCROLL, "item/fire_wall_scroll"),
                descriptor(ModItems.FIREBALL_SCROLL, "item/fireball_scroll"),
                descriptor(ModItems.FIREMAN_SCROLL, "item/fireman_scroll"),
                descriptor(ModItems.HEART_STONE, "item/heart_stone"),
                descriptor(BaseMaterialItems.ITEMS.get("ice_crystal"), "item/ice_crystal"),
                descriptor(ModItems.INVISIBLE_UNIVERSAL_LEAD, "item/invisible_universal_lead"),
                descriptor(BaseMaterialItems.ITEMS.get("iron_gold"), "item/iron_gold"),
                descriptor(BaseMaterialItems.ITEMS.get("lj"), "item/lj"),
                descriptor(ModItems.SLOWNESS_SCROLL, "item/slowness_scroll"),
                descriptor(BaseMaterialItems.ITEMS.get("sorcerer_stone"), "item/sorcerer_stone"),
                descriptor(BaseMaterialItems.ITEMS.get("strong_obsidian"), "item/strong_obsidian"),
                descriptor(ModItems.SUPER_BIG_FIREBALL_SCROLL, "item/super_big_fireball_scroll"),
                descriptor(ModItems.THOUSAND_BLOSSOMED_IMMORTAL_FRUIT, "item/thousand_blossomed_immortal_fruit"),
                descriptor(ModItems.UNIVERSAL_LEAD, "item/universal_lead"),
                descriptor(ModItems.INCUBATION_EGG, "item/incubation_egg"),
                descriptor(ModItems.LITTLE_PERSON_HAMMER, "item/little_person_hammer"),
                assetDescriptor(ModItems.METEORICORE_AXE, "solarfissure_items/meteoricore_axe"),
                descriptor(ModItems.MONEY_GUN, "item/money_gun")
        );
    }
}
