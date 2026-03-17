package com.kltyton.mob_battle.event.item;

import com.kltyton.mob_battle.items.manager.AreaGravityFieldManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class AreaGravityDeviceItemEvent {
    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(AreaGravityFieldManager::tickWorld);
    }
}
