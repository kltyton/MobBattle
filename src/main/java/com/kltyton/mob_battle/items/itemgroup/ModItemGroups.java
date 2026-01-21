package com.kltyton.mob_battle.items.itemgroup;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.enchantment.ModEnchantments;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.items.misc.BaseItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {


    public static void init() {
        // 添加到原版"生成蛋"物品组
        //ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModItemGroups::addSpawnEggsToTab);
        Registry.register(Registries.ITEM_GROUP, MOB_BATTLE_GROUP_KEY, MOB_BATTLE_GROUP);
    }
    public static final RegistryKey<ItemGroup> MOB_BATTLE_GROUP_KEY =
            RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(Mob_battle.MOD_ID, "main"));
    public static final ItemGroup MOB_BATTLE_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.BIG_FIREBALL_SCROLL))
            .displayName(Text.translatable("itemGroup.mob_battle.main"))
            .entries((context, entries) -> {
                RegistryWrapper.Impl<Enchantment> lookup = context.lookup().getOrThrow(RegistryKeys.ENCHANTMENT);

                entries.add(ModItems.MUTUAL_ATTACK_STICK);
                entries.add(ModItems.MASTER_SCEPTER);
                entries.add(ModItems.UNIVERSAL_LEAD);
                entries.add(ModItems.INVISIBLE_UNIVERSAL_LEAD);
                entries.add(ModItems.FIREBALL_SCROLL);
                entries.add(ModItems.BIG_FIREBALL_SCROLL);
                entries.add(ModItems.SUPER_BIG_FIREBALL_SCROLL);
                entries.add(ModItems.FIREMAN_SCROLL);
                entries.add(ModItems.SLOWNESS_SCROLL);
                entries.add(ModItems.FIRE_WALL_SCROLL);
                entries.add(ModItems.HEART_STONE);
                entries.add(ModItems.THOUSAND_BLOSSOMED_IMMORTAL_FRUIT);
                // 添加刷怪蛋
                entries.add(ModItems.HIGHBIRD_BABY_SPAWN_EGG);
                entries.add(ModItems.HIGHBIRD_TEENAGE_SPAWN_EGG);
                entries.add(ModItems.HIGHBIRD_EGG_SPAWN_EGG);
                entries.add(ModItems.HIGHBIRD_ADULT_SPAWN_EGG);
                entries.add(ModItems.XU_SHENG_SPAWN_EGG);
                entries.add(ModItems.DEEP_CREATURE_SPAWN_EGG);
                entries.add(ModItems.WITHER_SKELETON_KING_SPAWN_EGG);
                entries.add(ModItems.ARCHER_VILLAGER_SPAWN_EGG);
                entries.add(ModItems.WARRIOR_VILLAGER_SPAWN_EGG);
                entries.add(ModItems.BLUE_IRON_GOLEM_SPAWN_EGG);
                entries.add(ModItems.SUGAR_MAN_SCORPION_SPAWN_EGG);
                entries.add(ModItems.IRON_GOLEM_SPAWN_EGG);
                entries.add(ModItems.LITTLE_PERSON_CIVILIAN_SPAWN_EGG);
                entries.add(ModItems.LITTLE_PERSON_MILITIA_SPAWN_EGG);
                entries.add(ModItems.LITTLE_PERSON_ARCHER_SPAWN_EGG);
                entries.add(ModItems.LITTLE_PERSON_GIANT_SPAWN_EGG);
                entries.add(ModItems.LITTLE_PERSON_GUARD_SPAWN_EGG);
                entries.add(ModItems.LITTLE_PERSON_KING_SPAWN_EGG);
                entries.add(ModItems.VILLAGER_KING_SPAWN_EGG);
                entries.add(ModItems.VOID_CELL_SPAWN_EGG);
                entries.add(ModItems.SKULL_KING_SPAWN_EGG);
                entries.add(ModItems.SKULL_ARCHER_SPAWN_EGG);
                entries.add(ModItems.SKULL_WARRIOR_SPAWN_EGG);
                entries.add(ModItems.SKULL_MAGE_SPAWN_EGG);
                entries.add(ModItems.YOUNG_MIN_SPAWN_EGG);
                entries.add(ModItems.HIDDEN_EYE_SPAWN_EGG);
                entries.add(ModItems.VINDICATOR_GENERAL_SPAWN_EGG);

                for (SpawnEggItem item : ModItems.SPAWN_EGG_ITEMS.values()) {
                    entries.add(item);
                }
                //添加孵化蛋
                entries.add(ModItems.INCUBATION_EGG);

                //添加盔甲
                entries.add(ModItems.HELL_HELMET_1);
                entries.add(ModItems.HELL_CHESTPLATE_1);
                entries.add(ModItems.HELL_LEGGINGS_1);
                entries.add(ModItems.HELL_BOOTS_1);
                entries.add(ModItems.HELL_HELMET_2);
                entries.add(ModItems.HELL_CHESTPLATE_2);
                entries.add(ModItems.HELL_LEGGINGS_2);
                entries.add(ModItems.HELL_BOOTS_2);

                ItemStack enchantedHelmet = new ItemStack(ModItems.IRON_GOLD_HELMET);
                ItemStack enchantedChestplate = new ItemStack(ModItems.IRON_GOLD_CHESTPLATE);
                ItemStack enchantedLeggings = new ItemStack(ModItems.IRON_GOLD_LEGGINGS);
                ItemStack enchantedBoots = new ItemStack(ModItems.IRON_GOLD_BOOTS);
                enchantedHelmet.addEnchantment(new EnchantmentLevelEntry(lookup.getOrThrow(Enchantments.PROTECTION), 5).enchantment(), 5);
                enchantedChestplate.addEnchantment(new EnchantmentLevelEntry(lookup.getOrThrow(Enchantments.PROTECTION), 5).enchantment(), 5);
                enchantedLeggings.addEnchantment(new EnchantmentLevelEntry(lookup.getOrThrow(Enchantments.PROTECTION), 5).enchantment(), 5);
                enchantedBoots.addEnchantment(new EnchantmentLevelEntry(lookup.getOrThrow(Enchantments.PROTECTION), 5).enchantment(), 5);
                enchantedHelmet.addEnchantment(new EnchantmentLevelEntry(lookup.getOrThrow(ModEnchantments.MAGIC_PROTECTION), 3).enchantment(), 3);
                enchantedChestplate.addEnchantment(new EnchantmentLevelEntry(lookup.getOrThrow(ModEnchantments.MAGIC_PROTECTION), 3).enchantment(), 3);
                enchantedLeggings.addEnchantment(new EnchantmentLevelEntry(lookup.getOrThrow(ModEnchantments.MAGIC_PROTECTION), 3).enchantment(), 3);
                enchantedBoots.addEnchantment(new EnchantmentLevelEntry(lookup.getOrThrow(ModEnchantments.MAGIC_PROTECTION), 1).enchantment(), 1);
                enchantedChestplate.addEnchantment(new EnchantmentLevelEntry(lookup.getOrThrow(Enchantments.FIRE_PROTECTION), 1).enchantment(), 1);
                ModMaterial.createArmor(enchantedHelmet, ModMaterial.IRON_GOLD_INSTANCE, "iron_gold_helmet", EquipmentType.HELMET);
                ModMaterial.createArmor(enchantedChestplate, ModMaterial.IRON_GOLD_INSTANCE, "iron_gold_chestplate", EquipmentType.CHESTPLATE);
                ModMaterial.createArmor(enchantedLeggings, ModMaterial.IRON_GOLD_INSTANCE, "iron_gold_leggings", EquipmentType.LEGGINGS);
                ModMaterial.createArmor(enchantedBoots, ModMaterial.IRON_GOLD_INSTANCE, "iron_gold_boots", EquipmentType.BOOTS);
                entries.add(enchantedHelmet);
                entries.add(enchantedChestplate);
                entries.add(enchantedLeggings);
                entries.add(enchantedBoots);

                entries.add(ModItems.ECREDCULTIST_HELMET);
                entries.add(ModItems.ECREDCULTIST_CHESTPLATE);
                entries.add(ModItems.ECREDCULTIST_LEGGINGS);
                entries.add(ModItems.ECREDCULTIST_BOOTS);

                //添加基础武器
                entries.add(ModItems.METEORICORE_AXE);
                entries.add(ModItems.METEORICORE_BOW);
                entries.add(ModItems.METEORICORE_SWORD);

                ItemStack enchantedSword = new ItemStack(ModItems.IRON_GOLD_SWORD);
                enchantedSword.addEnchantment(new EnchantmentLevelEntry(lookup.getOrThrow(Enchantments.SWEEPING_EDGE), 1).enchantment(), 1);
                ModMaterial.createSword(enchantedSword, ModMaterial.IRON_GOLD_TOOL_MATERIAL, "iron_gold_sword", AttributeModifierSlot.MAINHAND);
                entries.add(enchantedSword);

                entries.add(ModItems.VS_SNIPE);
                //添加基础物品
                for (Item item : BaseItems.ITEMS.values()) {
                    entries.add(item);
                }
                for (Item item : ModItems.ITEMS.values()) {
                    entries.add(item);
                }
                //添加方块
                for (Block block : ModBlocks.BLOCKS.values()) {
                    entries.add(block.asItem());
                }
            })
            .build();
/*    private static void addSpawnEggsToTab(FabricItemGroupEntries entries) {
        // 添加高脚鸟系列的刷怪蛋
        entries.add(ModItems.HIGHBIRD_BABY_SPAWN_EGG);
        entries.add(ModItems.HIGHBIRD_TEENAGE_SPAWN_EGG);
        entries.add(ModItems.HIGHBIRD_EGG_SPAWN_EGG);
        entries.add(ModItems.HIGHBIRD_ADULT_SPAWN_EGG);
        entries.add(ModItems.XU_SHENG_SPAWN_EGG);
        entries.add(ModItems.ARCHER_VILLAGER_SPAWN_EGG);
        entries.add(ModItems.WARRIOR_VILLAGER_SPAWN_EGG);
    }*/
}
