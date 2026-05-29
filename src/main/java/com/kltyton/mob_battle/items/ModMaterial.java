package com.kltyton.mob_battle.items;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.tags.ModTags;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class ModMaterial {
    public static final ResourceKey<EquipmentAsset> HELL_ARMOR_MATERIAL_KEY_1 =
            ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_1"));
    public static final ResourceKey<EquipmentAsset> HELL_ARMOR_MATERIAL_KEY_2 =
            ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hell_2"));
    public static final ResourceKey<EquipmentAsset> IRON_GOLD_KEY =
            ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "iron_gold"));
    public static final ResourceKey<EquipmentAsset> ECREDCULTIST_KEY =
            ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "ecredcultist"));
    public static final ResourceKey<EquipmentAsset> EMERALD_DIAMOND_ALLOY_KEY =
            ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "emerald_diamond_alloy"));
    public static final ResourceKey<EquipmentAsset> ZIJIN_ARMOR_MATERIAL_KEY =
            ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "zijin"));
    public static final ResourceKey<EquipmentAsset> COMPRESSED_IRON_ARMOR_MATERIAL_KEY =
            ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "compressed_iron_ingot"));
    public static final ResourceKey<EquipmentAsset> COMPRESSED_GOLD_ARMOR_MATERIAL_KEY =
            ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "compressed_gold"));
    public static final ResourceKey<EquipmentAsset> COMPRESSED_DIAMOND_ARMOR_MATERIAL_KEY =
            ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "compressed_diamond"));
    public static final ResourceKey<EquipmentAsset> COMPRESSED_NETHERITE_ARMOR_MATERIAL_KEY =
            ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "compressed_netherite"));

    public static final ArmorMaterial HELL_ARMOR_INSTANCE_1 = new ArmorMaterial(
            Integer.MAX_VALUE,
            Map.of(
                    ArmorType.BOOTS,
                    1,
                    ArmorType.LEGGINGS,
                    2,
                    ArmorType.CHESTPLATE,
                    3,
                    ArmorType.HELMET,
                    1,
                    ArmorType.BODY,
                    2
            ),
            15,
            SoundEvents.ARMOR_EQUIP_IRON,
            0.0F,
            0.0F,
            ItemTags.REPAIRS_LEATHER_ARMOR,
            HELL_ARMOR_MATERIAL_KEY_1
    );
    public static final ArmorMaterial HELL_ARMOR_INSTANCE_2 = new ArmorMaterial(
            Integer.MAX_VALUE,
            Map.of(
                    ArmorType.BOOTS,
                    1,
                    ArmorType.LEGGINGS,
                    2,
                    ArmorType.CHESTPLATE,
                    3,
                    ArmorType.HELMET,
                    1,
                    ArmorType.BODY,
                    2
            ),
            15,
            SoundEvents.ARMOR_EQUIP_IRON,
            0.0F,
            0.0F,
            ItemTags.REPAIRS_LEATHER_ARMOR,
            HELL_ARMOR_MATERIAL_KEY_2
    );

    public static final ArmorMaterial IRON_GOLD_INSTANCE = new ArmorMaterial(
            512,
            Map.of(
                    ArmorType.BOOTS,
                    5,
                    ArmorType.LEGGINGS,
                    8,
                    ArmorType.CHESTPLATE,
                    6,
                    ArmorType.HELMET,
                    6,
                    ArmorType.BODY,
                    1
            ),
            15,
            SoundEvents.ARMOR_EQUIP_IRON,
            5f,
            0.1F,
            ModTags.IRON_GOLD_REPAIRABLE,
            IRON_GOLD_KEY
    );

    public static final ArmorMaterial ECREDCULTIST_INSTANCE = new ArmorMaterial(
            1,
            Map.of(
                    ArmorType.BOOTS,
                    1,
                    ArmorType.LEGGINGS,
                    1,
                    ArmorType.CHESTPLATE,
                    1,
                    ArmorType.HELMET,
                    1,
                    ArmorType.BODY,
                    1
            ),
            1,
            SoundEvents.ARMOR_EQUIP_IRON,
            1f,
            0.1F,
            ModTags.IRON_GOLD_REPAIRABLE,
            ECREDCULTIST_KEY
    );

    public static final ArmorMaterial EMERALD_DIAMOND_ALLOY_INSTANCE = new ArmorMaterial(
            Integer.MAX_VALUE,
            Map.of(
                    ArmorType.HELMET, 7,
                    ArmorType.CHESTPLATE, 14,
                    ArmorType.LEGGINGS, 10,
                    ArmorType.BOOTS, 7,
                    ArmorType.BODY, 0
            ),
            30,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            5.0F,
            0.1F,
            ItemTags.BEACON_PAYMENT_ITEMS,
            EMERALD_DIAMOND_ALLOY_KEY
    );

    public static final ArmorMaterial ZIJIN_ARMOR_INSTANCE = new ArmorMaterial(
            Integer.MAX_VALUE,
            Map.of(
                    ArmorType.HELMET, 7,
                    ArmorType.CHESTPLATE, 10,
                    ArmorType.LEGGINGS, 8,
                    ArmorType.BOOTS, 5,
                    ArmorType.BODY, 0
            ),
            30,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            0F,
            0.1F,
            ItemTags.BEACON_PAYMENT_ITEMS,
            ZIJIN_ARMOR_MATERIAL_KEY
    );
    public static final ArmorMaterial COMPRESSED_IRON_ARMOR_INSTANCE = new ArmorMaterial(
            5000,
            Map.of(
                    ArmorType.HELMET, 4,
                    ArmorType.CHESTPLATE, 9,
                    ArmorType.LEGGINGS, 6,
                    ArmorType.BOOTS, 4,
                    ArmorType.BODY, 0
            ),
            15,
            SoundEvents.ARMOR_EQUIP_IRON,
            4.0F,
            0.0F,
            ItemTags.BEACON_PAYMENT_ITEMS,
            COMPRESSED_IRON_ARMOR_MATERIAL_KEY
    );
    public static final ArmorMaterial COMPRESSED_GOLD_ARMOR_INSTANCE = new ArmorMaterial(
            4000,
            Map.of(
                    ArmorType.HELMET, 3,
                    ArmorType.CHESTPLATE, 8,
                    ArmorType.LEGGINGS, 7,
                    ArmorType.BOOTS, 3,
                    ArmorType.BODY, 0
            ),
            25,
            SoundEvents.ARMOR_EQUIP_GOLD,
            4.0F,
            0.0F,
            ItemTags.BEACON_PAYMENT_ITEMS,
            COMPRESSED_GOLD_ARMOR_MATERIAL_KEY
    );
    public static final ArmorMaterial COMPRESSED_DIAMOND_ARMOR_INSTANCE = new ArmorMaterial(
            10000,
            Map.of(
                    ArmorType.HELMET, 5,
                    ArmorType.CHESTPLATE, 8,
                    ArmorType.LEGGINGS, 7,
                    ArmorType.BOOTS, 5,
                    ArmorType.BODY, 0
            ),
            10,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            4.0F,
            0.0F,
            ItemTags.BEACON_PAYMENT_ITEMS,
            COMPRESSED_DIAMOND_ARMOR_MATERIAL_KEY
    );
    public static final ArmorMaterial COMPRESSED_NETHERITE_ARMOR_INSTANCE = new ArmorMaterial(
            20000,
            Map.of(
                    ArmorType.HELMET, 6,
                    ArmorType.CHESTPLATE, 10,
                    ArmorType.LEGGINGS, 8,
                    ArmorType.BOOTS, 6,
                    ArmorType.BODY, 0
            ),
            15,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            5.0F,
            0.1F,
            ItemTags.BEACON_PAYMENT_ITEMS,
            COMPRESSED_NETHERITE_ARMOR_MATERIAL_KEY
    );

    public static final ToolMaterial KLTYTON_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL,
            1561,
            8.0F,
            3.0F,
            10,
            null
    );
    public static final ToolMaterial IRON_GOLD_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            20000,
            8.0F,
            0.0F,
            10,
            ModTags.IRON_GOLD_REPAIRABLE
    );
    public static final ToolMaterial EMERALD_DIAMOND_ALLOY_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            20000,
            8.0F,
            0.0F,
            10,
            ItemTags.BEACON_PAYMENT_ITEMS
    );
    public static final ToolMaterial ZIJIN_ARMOR_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            20000,
            8.0F,
            0.0F,
            10,
            ItemTags.BEACON_PAYMENT_ITEMS
    );
    public static final ToolMaterial COMPRESSED_IRON_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            5000,
            8.0F,
            -1.0F,
            15,
            ItemTags.BEACON_PAYMENT_ITEMS
    );
    public static final ToolMaterial COMPRESSED_GOLD_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_GOLD_TOOL,
            4000,
            12.0F,
            -1.0F,
            25,
            ItemTags.BEACON_PAYMENT_ITEMS
    );
    public static final ToolMaterial COMPRESSED_DIAMOND_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            10000,
            8.0F,
            -1.0F,
            10,
            ItemTags.BEACON_PAYMENT_ITEMS
    );
    public static final ToolMaterial COMPRESSED_NETHERITE_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            15000,
            9.0F,
            -1.0F,
            15,
            ItemTags.BEACON_PAYMENT_ITEMS
    );

    public static void createArmor(ItemStack armor, ArmorMaterial armorMaterial, String id, ArmorType type) {
        EquipmentSlotGroup slot = EquipmentSlotGroup.bySlot(type.getSlot());
        ItemAttributeModifiers current = armor.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        if (armorMaterial == IRON_GOLD_INSTANCE) {
            armor.update(DataComponents.ATTRIBUTE_MODIFIERS, current, builder -> {
                //最大生命值 +7.0
                builder = builder.withModifierAdded(Attributes.MAX_HEALTH,
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "health_" + id), 13.0, AttributeModifier.Operation.ADD_VALUE), slot);
                // 攻击伤害 +7.5
                builder = builder.withModifierAdded(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "damage_" + id), 10.0, AttributeModifier.Operation.ADD_VALUE), slot);
                builder = builder.withModifierAdded(Attributes.MAX_ABSORPTION,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "absorption_" + id), 5.0, AttributeModifier.Operation.ADD_VALUE), slot);
                // 实体交互距离 +0.2
                builder = builder.withModifierAdded(Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "range_" + id), 0.2, AttributeModifier.Operation.ADD_VALUE), slot);
                if (type == ArmorType.BOOTS)
                    builder = builder.withModifierAdded(Attributes.STEP_HEIGHT,
                            new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "step_" + id), 2, AttributeModifier.Operation.ADD_VALUE), slot);
                return builder;
            });
        }
        if (armorMaterial == EMERALD_DIAMOND_ALLOY_INSTANCE) {
            armor.update(DataComponents.ATTRIBUTE_MODIFIERS, current, builder -> {
                //最大生命值 +20.0
                builder = builder.withModifierAdded(Attributes.MAX_HEALTH,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "health_" + id), 20, AttributeModifier.Operation.ADD_VALUE), slot);
                if (type == ArmorType.BOOTS)
                    builder = builder.withModifierAdded(Attributes.STEP_HEIGHT,
                            new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "step_" + id), 2, AttributeModifier.Operation.ADD_VALUE), slot);
                return builder;
            });
        }
        if (armorMaterial == ZIJIN_ARMOR_INSTANCE) {
            armor.update(DataComponents.ATTRIBUTE_MODIFIERS, current, builder -> {
                //最大生命值 +7.0
                builder = builder.withModifierAdded(Attributes.MAX_HEALTH,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "health_" + id), 10.0, AttributeModifier.Operation.ADD_VALUE), slot);
                if (type == ArmorType.HELMET)
                    builder = builder.withModifierAdded(Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "armor_toughness_" + id), 5.0, AttributeModifier.Operation.ADD_VALUE), slot);
                if (type == ArmorType.CHESTPLATE)
                    builder = builder.withModifierAdded(Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "armor_toughness_" + id), 6.0, AttributeModifier.Operation.ADD_VALUE), slot);
                if (type == ArmorType.LEGGINGS)
                    builder = builder.withModifierAdded(Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "armor_toughness_" + id), 6.0, AttributeModifier.Operation.ADD_VALUE), slot);
                if (type == ArmorType.BOOTS)
                    builder = builder.withModifierAdded(Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "armor_toughness_" + id), 3.0, AttributeModifier.Operation.ADD_VALUE), slot);
                return builder;
            });
        }
    }
    public static void createSword(ItemStack sword, ToolMaterial toolMaterial, String id, EquipmentSlotGroup slot) {
        ItemAttributeModifiers current = sword.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        if (toolMaterial == IRON_GOLD_TOOL_MATERIAL) {
            sword.update(DataComponents.ATTRIBUTE_MODIFIERS, current, updated -> {
                updated = updated.withModifierAdded(
                        Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "range_" + id), 0.6, AttributeModifier.Operation.ADD_VALUE),
                        slot
                );
                updated = updated.withModifierAdded(
                        Attributes.STEP_HEIGHT,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "step_" + id), 2, AttributeModifier.Operation.ADD_VALUE),
                        slot
                );
                updated = updated.withModifierAdded(
                        Attributes.SWEEPING_DAMAGE_RATIO,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "swipe_" + id), 1.0, AttributeModifier.Operation.ADD_VALUE),
                        slot
                );
                updated = updated.withModifierAdded(
                        Attributes.ARMOR,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "armor_" + id), 3.0, AttributeModifier.Operation.ADD_VALUE),
                        slot
                );
                return updated;
            });
        } else if (toolMaterial == EMERALD_DIAMOND_ALLOY_TOOL_MATERIAL) {
            sword.update(DataComponents.ATTRIBUTE_MODIFIERS, current, updated -> {
                updated = updated.withModifierAdded(
                        Attributes.SWEEPING_DAMAGE_RATIO,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "swipe_" + id), 1.0, AttributeModifier.Operation.ADD_VALUE),
                        slot
                );
                return updated;
            });
        } else if (toolMaterial == ZIJIN_ARMOR_TOOL_MATERIAL) {
            sword.update(DataComponents.ATTRIBUTE_MODIFIERS, current, updated -> {
                updated = updated.withModifierAdded(
                        Attributes.MAX_ABSORPTION,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "absorption_" + id), 80.0, AttributeModifier.Operation.ADD_VALUE),
                        slot
                );
                updated = updated.withModifierAdded(
                        Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "range_" + id), 1.0, AttributeModifier.Operation.ADD_VALUE),
                        slot
                );
                return updated;
            });
        }

    }
}
