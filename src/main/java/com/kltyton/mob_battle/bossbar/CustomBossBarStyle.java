package com.kltyton.mob_battle.bossbar;

import net.minecraft.resources.Identifier;

public record CustomBossBarStyle(
        Identifier id,
        Identifier backgroundTexture,
        Identifier progressTexture,
        int textureWidth,
        int textureHeight,
        int renderWidth,
        int renderHeight
) {
}
