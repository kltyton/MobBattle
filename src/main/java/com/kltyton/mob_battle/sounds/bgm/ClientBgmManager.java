package com.kltyton.mob_battle.sounds.bgm;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ClientBgmManager {
    @Nullable
    public static Identifier forcedMusicId = null;
    public static float forcedVolume = 1.0F;
}
