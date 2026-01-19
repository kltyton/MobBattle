package com.kltyton.mob_battle.items.misc;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.HashMap;
import java.util.Map;

public class BaseItems {
    // 存储所有注册的物品
    public static final Map<String, Item> ITEMS = new HashMap<>();
    private static final String[] UNCOMMON_ITEM_IDS = {
            "strong_obsidian",
            "fire_red",
            "blue_ice",
            "emerald_diamond",
            "iron_gold"
    };
    private static final String[] RARE_ITEM_IDS = {
            "energy",
            "sorcerer_stone"
    };
    private static final String[] EPIC_ITEM_IDS = {
            "lj",
            "fire_crystal",
            "ice_crystal",
            "desert_crystal"
    };

    public static void init() {
        for (String id : UNCOMMON_ITEM_IDS) {
            registerItem(id, Rarity.UNCOMMON);
        }
        for (String id : RARE_ITEM_IDS) {
            registerItem(id, Rarity.RARE);
        }
        for (String id : EPIC_ITEM_IDS) {
            registerItem(id, Rarity.EPIC);
        }
    }

    private static void registerItem(String id, Rarity rarity) {
        Identifier itemId = Identifier.of(Mob_battle.MOD_ID, id);
        Item.Settings settings = new Item.Settings().rarity(rarity)
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, itemId));
        Item item = Registry.register(Registries.ITEM, itemId, new Item(settings));
        ITEMS.put(id, item);
    }

    // 静态引用
    public static final Item BLUE_ICE = ITEMS.get("blue_ice");
    public static final Item ENERGY = ITEMS.get("energy");
    public static final Item IRON_GOLD = ITEMS.get("iron_gold");
}