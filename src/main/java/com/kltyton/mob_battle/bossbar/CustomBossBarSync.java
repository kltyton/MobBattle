package com.kltyton.mob_battle.bossbar;

import com.kltyton.mob_battle.network.packet.CustomBossBarPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public final class CustomBossBarSync {
    private CustomBossBarSync() {
    }

    public static void add(ServerPlayerEntity player, UUID bossBarUuid, Identifier styleId) {
        if (ServerPlayNetworking.canSend(player, CustomBossBarPayload.ID)) {
            ServerPlayNetworking.send(player, new CustomBossBarPayload(bossBarUuid, styleId, true));
        }
    }

    public static void remove(ServerPlayerEntity player, UUID bossBarUuid) {
        if (ServerPlayNetworking.canSend(player, CustomBossBarPayload.ID)) {
            ServerPlayNetworking.send(player, new CustomBossBarPayload(bossBarUuid, Identifier.ofVanilla("empty"), false));
        }
    }
}
