package com.kltyton.mob_battle.animation;

import com.kltyton.mob_battle.network.packet.PalMorePlayerAnimationPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class PalMorePlayerAnimationServerHandler {
    private PalMorePlayerAnimationServerHandler() {
    }

    public static boolean play(ServerPlayer animatedPlayer, Identifier animationId) {
        MinecraftServer server = animatedPlayer.level().getServer();
        if (server == null) {
            return false;
        }

        PalMorePlayerAnimationPayload payload =
                new PalMorePlayerAnimationPayload(animatedPlayer.getId(), animationId);
        boolean sent = false;
        for (ServerPlayer viewer : server.getPlayerList().getPlayers()) {
            if (viewer.level() == animatedPlayer.level()
                    && ServerPlayNetworking.canSend(viewer, PalMorePlayerAnimationPayload.ID)) {
                ServerPlayNetworking.send(viewer, payload);
                sent = true;
            }
        }
        return sent;
    }
}
