package com.kltyton.mob_battle.config.whitelist;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Set;
import java.util.UUID;

public final class ModPlayerWhitelist {

    private static final Set<UUID> WHITELIST = Set.of(
            UUID.fromString("fd6607c5-37f5-3adb-9e88-07e81e489a35"),
            UUID.fromString("c75d71b7-52b5-3396-afee-0a9a3b41f186"),
            UUID.fromString("3d5cda46-23c0-4b7a-a3cd-2e82cc587984"),
            UUID.fromString("4d52859d-2a05-4952-b0a3-375a16a05d18")
    );

    public static boolean isWhitelisted(UUID uuid) {
        return WHITELIST.contains(uuid);
    }

    public static boolean isWhitelisted(ServerPlayerEntity player) {
        return player != null && isWhitelisted(player.getUuid());
    }
}
