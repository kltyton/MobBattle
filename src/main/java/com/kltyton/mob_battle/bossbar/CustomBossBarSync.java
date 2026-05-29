package com.kltyton.mob_battle.bossbar;

import com.kltyton.mob_battle.network.packet.CustomBossBarPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

public final class CustomBossBarSync {
    private CustomBossBarSync() {
    }

    public static void add(ServerPlayer player, UUID bossBarUuid, ResourceLocation styleId) {
        if (ServerPlayNetworking.canSend(player, CustomBossBarPayload.ID)) {
            ServerPlayNetworking.send(player, new CustomBossBarPayload(bossBarUuid, styleId, true));
        }
    }

    public static void remove(ServerPlayer player, UUID bossBarUuid) {
        if (ServerPlayNetworking.canSend(player, CustomBossBarPayload.ID)) {
            ServerPlayNetworking.send(player, new CustomBossBarPayload(bossBarUuid, ResourceLocation.withDefaultNamespace("empty"), false));
        }
    }
}
