package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.Mob_battle;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ModMainEvents {
    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Mob_battle.SERVER = server;
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            Mob_battle.SERVER = null;
        });
    }
}
