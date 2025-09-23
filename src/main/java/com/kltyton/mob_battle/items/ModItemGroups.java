package com.kltyton.mob_battle.items;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.misc.BaseItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static void init() {
        // 添加到原版"生成蛋"物品组
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModItemGroups::addSpawnEggsToTab);
        Registry.register(Registries.ITEM_GROUP, Identifier.of(Mob_battle.MOD_ID, "main"), MOB_BATTLE_GROUP);
    }
    public static final ItemGroup MOB_BATTLE_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.BIG_FIREBALL_SCROLL))
            .displayName(Text.translatable("itemGroup.mob_battle.main"))
            .entries((context, entries) -> {
                entries.add(ModItems.MUTUAL_ATTACK_STICK);
                entries.add(ModItems.UNIVERSAL_LEAD);
                entries.add(ModItems.FIREBALL_SCROLL);
                entries.add(ModItems.BIG_FIREBALL_SCROLL);
                entries.add(ModItems.FIREMAN_SCROLL);
                entries.add(ModItems.HEART_STONE);
                // 添加刷怪蛋
                entries.add(ModItems.HIGHBIRD_BABY_SPAWN_EGG);
                entries.add(ModItems.HIGHBIRD_TEENAGE_SPAWN_EGG);
                entries.add(ModItems.HIGHBIRD_EGG_SPAWN_EGG);
                entries.add(ModItems.HIGHBIRD_ADULT_SPAWN_EGG);
                entries.add(ModItems.XU_SHENG_SPAWN_EGG);
                entries.add(ModItems.ARCHER_VILLAGER_SPAWN_EGG);
                entries.add(ModItems.WARRIOR_VILLAGER_SPAWN_EGG);
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
                //添加基础武器
                entries.add(ModItems.METEORICORE_AXE);
                entries.add(ModItems.METEORICORE_BOW);
                entries.add(ModItems.METEORICORE_SWORD);
                //添加基础物品
                for (Item item : BaseItems.ITEMS.values()) {
                    entries.add(item);
                }
            })
            .build();
    private static void addSpawnEggsToTab(FabricItemGroupEntries entries) {
        // 添加高脚鸟系列的刷怪蛋
        entries.add(ModItems.HIGHBIRD_BABY_SPAWN_EGG);
        entries.add(ModItems.HIGHBIRD_TEENAGE_SPAWN_EGG);
        entries.add(ModItems.HIGHBIRD_EGG_SPAWN_EGG);
        entries.add(ModItems.HIGHBIRD_ADULT_SPAWN_EGG);
        entries.add(ModItems.XU_SHENG_SPAWN_EGG);
        entries.add(ModItems.ARCHER_VILLAGER_SPAWN_EGG);
        entries.add(ModItems.WARRIOR_VILLAGER_SPAWN_EGG);
    }
}
