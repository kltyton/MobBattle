package com.kltyton.mob_battle.items;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ModMaterial {
    public static final RegistryKey<EquipmentAsset> HELL_ARMOR_MATERIAL_KEY_1 =
            RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(Mob_battle.MOD_ID, "hell_1"));
    public static final RegistryKey<EquipmentAsset> HELL_ARMOR_MATERIAL_KEY_2 =
            RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(Mob_battle.MOD_ID, "hell_2"));
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
    public static final ToolMaterial KLTYTON_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL,
            1561,
            8.0F,
            3.0F,
            10,
            null
    );
}
