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

    // 新增：淡出计时器和状态
    public static int fadeOutTicks = 0;
    public static final int MAX_FADE_OUT_TICKS = 40;
    public static boolean isFadingOut = false;
    @Nullable
    public static Identifier fadingOutMusicId = null;
    public static float fadingOutVolume = 1.0F;

    // 重置淡出状态
    public static void resetFadeOut() {
        fadeOutTicks = 0;
        isFadingOut = false;
        fadingOutMusicId = null;
        fadingOutVolume = 1.0F;
    }

    // 开始淡出
    public static void startFadeOut(@Nullable Identifier currentMusicId, float currentVolume) {
        isFadingOut = true;
        fadeOutTicks = MAX_FADE_OUT_TICKS;
        fadingOutMusicId = currentMusicId;
        fadingOutVolume = currentVolume;
    }
}