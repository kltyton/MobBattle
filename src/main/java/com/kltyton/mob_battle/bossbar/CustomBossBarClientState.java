package com.kltyton.mob_battle.bossbar;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class CustomBossBarClientState {
    private static final Map<UUID, Identifier> BOSS_BAR_STYLES = new HashMap<>();

    private CustomBossBarClientState() {
    }

    public static void set(UUID bossBarUuid, Identifier styleId) {
        BOSS_BAR_STYLES.put(bossBarUuid, styleId);
    }

    public static void remove(UUID bossBarUuid) {
        BOSS_BAR_STYLES.remove(bossBarUuid);
    }

    public static Optional<CustomBossBarStyle> get(UUID bossBarUuid) {
        Identifier styleId = BOSS_BAR_STYLES.get(bossBarUuid);
        return styleId == null ? Optional.empty() : CustomBossBarStyles.get(styleId);
    }

    public static void clear() {
        BOSS_BAR_STYLES.clear();
    }
}
