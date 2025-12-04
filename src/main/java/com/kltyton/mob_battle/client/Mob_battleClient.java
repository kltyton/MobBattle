package com.kltyton.mob_battle.client;

import com.kltyton.mob_battle.client.keybinding.ModKeyBinding;
import com.kltyton.mob_battle.event.ModClientEvents;
import com.kltyton.mob_battle.network.ClientPlayNetwork;
import net.fabricmc.api.ClientModInitializer;

public class Mob_battleClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetwork.init();
        ModClientEvents.clientInit();
        ModEntityRenderInit.init();
        ModKeyBinding.init();
    }
}
