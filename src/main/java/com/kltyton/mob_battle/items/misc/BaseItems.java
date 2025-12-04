package com.kltyton.mob_battle.items.misc;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class BaseItems {
    // 存储所有注册的物品
    public static final Map<String, Item> ITEMS = new HashMap<>();

    // 物品标识符数组
    private static final String[] ITEM_IDS = {
            "blue_ice",
            "energy",
            "fire_crystal",
            "ice_crystal",
            "lj",
            "sorcerer_stone",
            "emerald_diamond",
            "iron_gold",
            "strong_obsidian",
            "fire_red",
            "desert_crystal"
    };

    public static void init() {
        for (String id : ITEM_IDS) {
            registerItem(id);
        }
    }

    private static void registerItem(String id) {
        // 创建物品标识符
        Identifier itemId = Identifier.of(Mob_battle.MOD_ID, id);
        // 创建物品设置并指定RegistryKey
        Item.Settings settings = new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, itemId));

        // 注册物品并存入Map
        Item item = Registry.register(Registries.ITEM, itemId, new Item(settings));
        ITEMS.put(id, item);
    }

    // 静态引用
    public static final Item BLUE_ICE = ITEMS.get("blue_ice");
    public static final Item ENERGY = ITEMS.get("energy");
    public static final Item IRON_GOLD = ITEMS.get("iron_gold");
}