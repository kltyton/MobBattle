package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.client.camera.ImbalanceCameraEffect;
import com.kltyton.mob_battle.config.whitelist.ClientPermissionState;
import com.kltyton.mob_battle.items.itemgroup.ClientItemGroupState;
import com.kltyton.mob_battle.sounds.bgm.ClientBgmManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

@Environment(EnvType.CLIENT)
public class ModClientEvents {
    public static void clientInit() {
        LeftClickEvent.init();
        BuffStunClientEvent.ClientInit();
        ImbalanceCameraEffect.init();
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            LeftClickEvent.reset();
            BuffStunClientEvent.reset();
            ClientPermissionState.reset();
            ClientItemGroupState.reset();
            ClientBgmManager.resetAll();
        });
    }
}
