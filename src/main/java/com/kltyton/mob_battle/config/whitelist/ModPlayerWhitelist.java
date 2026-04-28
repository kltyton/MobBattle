package com.kltyton.mob_battle.config.whitelist;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Set;
import java.util.UUID;

public final class ModPlayerWhitelist {

    private static final Set<UUID> WHITELIST = Set.of(
            UUID.fromString("fd6607c5-37f5-3adb-9e88-07e81e489a35"),
            UUID.fromString("c75d71b7-52b5-3396-afee-0a9a3b41f186")
    );

    public static boolean isWhitelisted(UUID uuid) {
        return WHITELIST.contains(uuid);
    }

    public static boolean isWhitelisted(ServerPlayerEntity player) {
        return player != null && isWhitelisted(player.getUuid());
    }
}
