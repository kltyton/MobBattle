package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import java.util.Map;
import java.util.List;

/** “有”套装的装备资产与基础属性；技能只认可四个正确装备槽中的同一套资产。 */
public final class PresenceArmorItems {
    public static final ArmorMaterial MATERIAL = new ArmorMaterial(20000,
            Map.of(ArmorType.HELMET, 6, ArmorType.CHESTPLATE, 10, ArmorType.LEGGINGS, 8,
                    ArmorType.BOOTS, 6, ArmorType.BODY, 0),
            15, SoundEvents.ARMOR_EQUIP_NETHERITE, 5.0F, 0.1F, ItemTags.BEACON_PAYMENT_ITEMS,
            ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "presence")));
    public static Item HELMET;
    public static Item CHESTPLATE;
    public static Item LEGGINGS;
    public static Item BOOTS;

    private PresenceArmorItems() {}

    /** 在物品注册阶段调用，生命加成沿用压缩下界合金的每件 10 点并再增加 10 点。 */
    public static void init() {
        HELMET = RegistrySupport.registerCompressedArmor("presence_helmet", MATERIAL, ArmorType.HELMET, 20000, 20, 0);
        CHESTPLATE = RegistrySupport.registerCompressedArmor("presence_chestplate", MATERIAL, ArmorType.CHESTPLATE, 20000, 20, 2);
        LEGGINGS = RegistrySupport.registerCompressedArmor("presence_leggings", MATERIAL, ArmorType.LEGGINGS, 20000, 20, 2);
        BOOTS = RegistrySupport.registerCompressedArmor("presence_boots", MATERIAL, ArmorType.BOOTS, 20000, 20, 0);
    }

    /** 注册完成后供创造栏、语言与标签生成器使用。 */
    public static List<Item> all() {
        return List.of(HELMET, CHESTPLATE, LEGGINGS, BOOTS);
    }
}
