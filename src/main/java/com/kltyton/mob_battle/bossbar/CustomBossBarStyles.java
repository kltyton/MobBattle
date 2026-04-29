package com.kltyton.mob_battle.bossbar;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class CustomBossBarStyles {
    public static final int DEFAULT_TEXTURE_WIDTH = 224;
    public static final int DEFAULT_TEXTURE_HEIGHT = 48;
    public static final int DEFAULT_RENDER_WIDTH = 224;
    public static final int DEFAULT_RENDER_HEIGHT = 48;

    public static final Identifier VINDICATOR_GENERAL = Identifier.of(Mob_battle.MOD_ID, "vindicator_general");
    public static final Identifier WITHER_SKELETON_KING = Identifier.of(Mob_battle.MOD_ID, "wither_skeleton_king");
    public static final Identifier HULKBUSTER = Identifier.of(Mob_battle.MOD_ID, "hulkbuster");

    private static final Map<Identifier, CustomBossBarStyle> STYLES = new HashMap<>();

    static {
        register(
                VINDICATOR_GENERAL,
                Identifier.of(Mob_battle.MOD_ID, "textures/gui/boss_bar/vindicator_general_background.png"),
                Identifier.of(Mob_battle.MOD_ID, "textures/gui/boss_bar/vindicator_general_progress.png")
        );
        register(
                WITHER_SKELETON_KING,
                Identifier.of(Mob_battle.MOD_ID, "textures/gui/boss_bar/wither_skeleton_king_background.png"),
                Identifier.of(Mob_battle.MOD_ID, "textures/gui/boss_bar/wither_skeleton_king_progress.png")
        );
        register(
                HULKBUSTER,
                Identifier.of(Mob_battle.MOD_ID, "textures/gui/boss_bar/hulkbuster_background.png"),
                Identifier.of(Mob_battle.MOD_ID, "textures/gui/boss_bar/hulkbuster_progress.png")
        );
    }

    private CustomBossBarStyles() {
    }

    public static CustomBossBarStyle register(Identifier id, Identifier backgroundTexture, Identifier progressTexture) {
        return register(
                id,
                backgroundTexture,
                progressTexture,
                DEFAULT_TEXTURE_WIDTH,
                DEFAULT_TEXTURE_HEIGHT,
                DEFAULT_RENDER_WIDTH,
                DEFAULT_RENDER_HEIGHT
        );
    }

    public static CustomBossBarStyle register(
            Identifier id,
            Identifier backgroundTexture,
            Identifier progressTexture,
            int textureWidth,
            int textureHeight,
            int renderWidth,
            int renderHeight
    ) {
        CustomBossBarStyle style = new CustomBossBarStyle(
                id,
                backgroundTexture,
                progressTexture,
                textureWidth,
                textureHeight,
                renderWidth,
                renderHeight
        );
        STYLES.put(id, style);
        return style;
    }

    public static Optional<CustomBossBarStyle> get(Identifier id) {
        return Optional.ofNullable(STYLES.get(id));
    }
}
