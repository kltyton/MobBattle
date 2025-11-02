package com.kltyton.mob_battle.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModClientEvents {
    public static void clientInit() {
        LeftClickEvent.init();
        BuffStunClientEvent.ClientInit();
    }
}
