package com.kltyton.mob_battle.config.whitelist;

import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;

public final class ModPlayerWhitelist {

    private static final Set<UUID> WHITELIST = Set.of(
            UUID.fromString("fd6607c5-37f5-3adb-9e88-07e81e489a35"),
            UUID.fromString("c75d71b7-52b5-3396-afee-0a9a3b41f186"),
            UUID.fromString("3d5cda46-23c0-4b7a-a3cd-2e82cc587984"),
            UUID.fromString("4d52859d-2a05-4952-b0a3-375a16a05d18"),
            UUID.fromString("3cba904d-c386-3648-8de0-fbbddae56dd2"),
            UUID.fromString("3b16dad9-7ff5-32fa-bfab-829dd874e94e"),
            UUID.fromString("bcabc91e-34e3-3b82-905a-49de3258a881")
    );

    public static boolean isWhitelisted(UUID uuid) {
        return WHITELIST.contains(uuid);
    }

    public static boolean isWhitelisted(ServerPlayer player) {
        return player != null && isWhitelisted(player.getUUID());
    }
}
