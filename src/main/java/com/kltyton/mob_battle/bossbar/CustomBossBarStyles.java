package com.kltyton.mob_battle.bossbar;

import com.kltyton.mob_battle.Mob_battle;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public final class CustomBossBarStyles {
    public static final int DEFAULT_TEXTURE_WIDTH = 224;
    public static final int DEFAULT_TEXTURE_HEIGHT = 32;
    public static final int DEFAULT_RENDER_WIDTH = 224;
    public static final int DEFAULT_RENDER_HEIGHT = 32;

    public static final ResourceLocation VINDICATOR_GENERAL = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "vindicator_general");
    public static final ResourceLocation WITHER_SKELETON_KING = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "wither_skeleton_king");
    public static final ResourceLocation HULKBUSTER = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hulkbuster");

    private static final Map<ResourceLocation, CustomBossBarStyle> STYLES = new HashMap<>();

    static {
        register(
                VINDICATOR_GENERAL,
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/gui/boss_bar/vindicator_general_background.png"),
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/gui/boss_bar/vindicator_general_progress.png")
        );
        register(
                WITHER_SKELETON_KING,
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/gui/boss_bar/wither_skeleton_king_background.png"),
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/gui/boss_bar/wither_skeleton_king_progress.png")
        );
        register(
                HULKBUSTER,
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/gui/boss_bar/hulkbuster_background.png"),
                ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/gui/boss_bar/hulkbuster_progress.png")
        );
    }

    private CustomBossBarStyles() {
    }

    public static CustomBossBarStyle register(ResourceLocation id, ResourceLocation backgroundTexture, ResourceLocation progressTexture) {
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
            ResourceLocation id,
            ResourceLocation backgroundTexture,
            ResourceLocation progressTexture,
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

    public static Optional<CustomBossBarStyle> get(ResourceLocation id) {
        return Optional.ofNullable(STYLES.get(id));
    }
}
