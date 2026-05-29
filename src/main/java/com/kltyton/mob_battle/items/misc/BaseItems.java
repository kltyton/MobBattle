package com.kltyton.mob_battle.items.misc;

import com.kltyton.mob_battle.Mob_battle;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

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
        ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, id);
        Item.Properties settings = new Item.Properties().rarity(rarity)
                .setId(ResourceKey.create(Registries.ITEM, itemId));
        Item item = Registry.register(BuiltInRegistries.ITEM, itemId, new Item(settings));
        ITEMS.put(id, item);
    }

    // 静态引用
    public static final Item BLUE_ICE = ITEMS.get("blue_ice");
    public static final Item ENERGY = ITEMS.get("energy");
    public static final Item IRON_GOLD = ITEMS.get("iron_gold");
}