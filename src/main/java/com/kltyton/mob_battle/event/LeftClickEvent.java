package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.network.packet.LeftClickPacket;
import com.kltyton.mob_battle.input.LeftClickDispatcher;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class LeftClickEvent {
    private static boolean wasPressed = false;
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            boolean isPressed = client.options.keyAttack.isDown();
            if (isPressed != wasPressed) {
                wasPressed = isPressed;
                Player player = client.player;
                ClientPlayNetworking.send(new LeftClickPacket(isPressed));
                LeftClickDispatcher.leftClick(player, isPressed, false);
            }
        });
    }
}
