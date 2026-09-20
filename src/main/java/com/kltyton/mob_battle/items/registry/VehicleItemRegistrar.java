package com.kltyton.mob_battle.items.registry;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.vehicle.ObsidianBoatItem;

/** 载具物品注册器。 */
public final class VehicleItemRegistrar {
    private VehicleItemRegistrar() {
    }

    public static void init() {
        ModItems.OBSIDIAN_BOAT = RegistrySupport.registerItem(
                "obsidian_boat",
                new ObsidianBoatItem(
                        ModEntities.OBSIDIAN_BOAT,
                        RegistrySupport.registryBaseItemSettings("obsidian_boat").stacksTo(1)
                ),
                true
        );
    }
}
