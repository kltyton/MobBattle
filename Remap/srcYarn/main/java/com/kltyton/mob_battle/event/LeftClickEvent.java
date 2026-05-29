package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.network.packet.LeftClickPacket;
import com.kltyton.mob_battle.utils.LeftClickUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

public class LeftClickEvent {
    private static boolean wasPressed = false;
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            boolean isPressed = GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            if (isPressed != wasPressed) {
                wasPressed = isPressed;
                PlayerEntity player = client.player;
                ClientPlayNetworking.send(new LeftClickPacket(isPressed));
                LeftClickUtil.leftClick(player, isPressed, false);
            }
        });
    }
}
