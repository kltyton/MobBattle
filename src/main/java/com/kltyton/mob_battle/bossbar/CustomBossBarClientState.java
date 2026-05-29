package com.kltyton.mob_battle.bossbar;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public final class CustomBossBarClientState {
    private static final Map<UUID, ResourceLocation> BOSS_BAR_STYLES = new HashMap<>();

    private CustomBossBarClientState() {
    }

    public static void set(UUID bossBarUuid, ResourceLocation styleId) {
        BOSS_BAR_STYLES.put(bossBarUuid, styleId);
    }

    public static void remove(UUID bossBarUuid) {
        BOSS_BAR_STYLES.remove(bossBarUuid);
    }

    public static Optional<CustomBossBarStyle> get(UUID bossBarUuid) {
        ResourceLocation styleId = BOSS_BAR_STYLES.get(bossBarUuid);
        return styleId == null ? Optional.empty() : CustomBossBarStyles.get(styleId);
    }

    public static void clear() {
        BOSS_BAR_STYLES.clear();
    }
}
