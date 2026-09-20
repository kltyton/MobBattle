package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.Mob_battle;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public final class BaseMaterialItems {
    private BaseMaterialItems() {
    }
    /**
     * 按稳定 ID 暴露早期注册的基础材料。注册表、语言、配方和 Datagen 共用该映射，
     * 避免为每个材料再维护一份可能在初始化前求值的静态字段。
     */
    public static final Map<String, Item> ITEMS = new HashMap<>();
    private static final String[] UNCOMMON_ITEM_IDS = {
            "strong_obsidian",
            "fire_red",
            "blue_ice"
    };
    private static final String[] RARE_ITEM_IDS = {
            "energy",
            "sorcerer_stone"
    };
    private static final String[] EPIC_ITEM_IDS = {
            "lj",
            "fire_crystal",
            "ice_crystal",
            "desert_crystal",
            "emerald_diamond",
            "iron_gold"
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
        Identifier itemId = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, id);
        Item.Properties settings = new Item.Properties().rarity(rarity)
                .setId(ResourceKey.create(Registries.ITEM, itemId));
        Item item = Registry.register(BuiltInRegistries.ITEM, itemId, new Item(settings));
        ITEMS.put(id, item);
    }

}
