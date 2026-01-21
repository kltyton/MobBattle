package com.kltyton.mob_battle.items;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ModMaterial {
    public static final RegistryKey<EquipmentAsset> HELL_ARMOR_MATERIAL_KEY_1 =
            RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(Mob_battle.MOD_ID, "hell_1"));
    public static final RegistryKey<EquipmentAsset> HELL_ARMOR_MATERIAL_KEY_2 =
            RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(Mob_battle.MOD_ID, "hell_2"));
    public static final RegistryKey<EquipmentAsset> IRON_GOLD_KEY =
            RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(Mob_battle.MOD_ID, "iron_gold"));
    public static final RegistryKey<EquipmentAsset> ECREDCULTIST_KEY =
            RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(Mob_battle.MOD_ID, "ecredcultist"));
    public static final TagKey<Item> IRON_GOLD_REPAIRABLE =  TagKey.of(RegistryKeys.ITEM, Identifier.of(Mob_battle.MOD_ID, "iron_gold_repairable"));
    public static final ArmorMaterial HELL_ARMOR_INSTANCE_1 = new ArmorMaterial(
            Integer.MAX_VALUE,
            Map.of(
                    EquipmentType.BOOTS,
                    1,
                    EquipmentType.LEGGINGS,
                    2,
                    EquipmentType.CHESTPLATE,
                    3,
                    EquipmentType.HELMET,
                    1,
                    EquipmentType.BODY,
                    2
            ),
            15,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON,
            0.0F,
            0.0F,
            ItemTags.REPAIRS_LEATHER_ARMOR,
            HELL_ARMOR_MATERIAL_KEY_1
    );
    public static final ArmorMaterial HELL_ARMOR_INSTANCE_2 = new ArmorMaterial(
            Integer.MAX_VALUE,
            Map.of(
                    EquipmentType.BOOTS,
                    1,
                    EquipmentType.LEGGINGS,
                    2,
                    EquipmentType.CHESTPLATE,
                    3,
                    EquipmentType.HELMET,
                    1,
                    EquipmentType.BODY,
                    2
            ),
            15,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON,
            0.0F,
            0.0F,
            ItemTags.REPAIRS_LEATHER_ARMOR,
            HELL_ARMOR_MATERIAL_KEY_2
    );

    public static final ArmorMaterial IRON_GOLD_INSTANCE = new ArmorMaterial(
            512,
            Map.of(
                    EquipmentType.BOOTS,
                    5,
                    EquipmentType.LEGGINGS,
                    8,
                    EquipmentType.CHESTPLATE,
                    6,
                    EquipmentType.HELMET,
                    6,
                    EquipmentType.BODY,
                    1
            ),
            15,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON,
            5f,
            1f,
            IRON_GOLD_REPAIRABLE,
            IRON_GOLD_KEY
    );
    public static final ArmorMaterial ECREDCULTIST_INSTANCE = new ArmorMaterial(
            1,
            Map.of(
                    EquipmentType.BOOTS,
                    1,
                    EquipmentType.LEGGINGS,
                    1,
                    EquipmentType.CHESTPLATE,
                    1,
                    EquipmentType.HELMET,
                    1,
                    EquipmentType.BODY,
                    1
            ),
            1,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON,
            1f,
            1f,
            IRON_GOLD_REPAIRABLE,
            ECREDCULTIST_KEY
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
            IRON_GOLD_REPAIRABLE
    );
    public static void createArmor(ItemStack armor, ArmorMaterial armorMaterial, String id, EquipmentType type) {
        AttributeModifierSlot slot = AttributeModifierSlot.forEquipmentSlot(type.getEquipmentSlot());
        AttributeModifiersComponent current = armor.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        if (armorMaterial == IRON_GOLD_INSTANCE) {
            armor.apply(DataComponentTypes.ATTRIBUTE_MODIFIERS, current, builder -> {
                //最大生命值 +7.0
                builder = builder.with(EntityAttributes.MAX_HEALTH,
                    new EntityAttributeModifier(Identifier.of(Mob_battle.MOD_ID, "health_" + id), 7.0, EntityAttributeModifier.Operation.ADD_VALUE), slot);
                // 攻击伤害 +7.5
                builder = builder.with(EntityAttributes.ATTACK_DAMAGE,
                        new EntityAttributeModifier(Identifier.of(Mob_battle.MOD_ID, "damage_" + id), 7.5, EntityAttributeModifier.Operation.ADD_VALUE), slot);
                // 实体交互距离 +0.2
                builder = builder.with(EntityAttributes.ENTITY_INTERACTION_RANGE,
                        new EntityAttributeModifier(Identifier.of(Mob_battle.MOD_ID, "range_" + id), 0.2, EntityAttributeModifier.Operation.ADD_VALUE), slot);
                if (type == EquipmentType.BOOTS)
                    builder = builder.with(EntityAttributes.STEP_HEIGHT,
                            new EntityAttributeModifier(Identifier.of(Mob_battle.MOD_ID, "step_" + id), 2, EntityAttributeModifier.Operation.ADD_VALUE), slot);
                return builder;
            });
        }
    }
    public static void createSword(ItemStack sword, ToolMaterial toolMaterial, String id, AttributeModifierSlot slot) {
        AttributeModifiersComponent current = sword.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        if (toolMaterial == IRON_GOLD_TOOL_MATERIAL) {
            sword.apply(DataComponentTypes.ATTRIBUTE_MODIFIERS, current, updated -> {
                updated = updated.with(
                        EntityAttributes.ENTITY_INTERACTION_RANGE,
                        new EntityAttributeModifier(Identifier.of(Mob_battle.MOD_ID, "range_" + id), 0.6, EntityAttributeModifier.Operation.ADD_VALUE),
                        slot
                );
                updated = updated.with(
                        EntityAttributes.STEP_HEIGHT,
                        new EntityAttributeModifier(Identifier.of(Mob_battle.MOD_ID, "step_" + id), 2, EntityAttributeModifier.Operation.ADD_VALUE),
                        slot
                );
                return updated;
            });
        }
    }
}
