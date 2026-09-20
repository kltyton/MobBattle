package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.heartstone.HeartStoneItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

/**
 * 心石物品注册器。
 *
 * <p>按原始源码语句顺序在心石字段位置注册,使用直接 {@code Registry.register},
 * 不写入 ITEMS/GENERATED_ITEMS,保持逐字兼容。</p>
 */
public final class HeartStoneItemRegistrar {

    private HeartStoneItemRegistrar() {
    }

    public static void init() {
        ModItems.HEART_STONE = Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "heart_stone"),
                new HeartStoneItem(new Item.Properties().stacksTo(3)
                        .setId(ResourceKey.create(
                                Registries.ITEM, Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "heart_stone")))));
    }
}
