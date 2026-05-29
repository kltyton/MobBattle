package com.kltyton.mob_battle.bossbar;

import net.minecraft.resources.ResourceLocation;

public record CustomBossBarStyle(
        ResourceLocation id,
        ResourceLocation backgroundTexture,
        ResourceLocation progressTexture,
        int textureWidth,
        int textureHeight,
        int renderWidth,
        int renderHeight
) {
}
