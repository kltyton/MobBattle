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

    // 淡出相关
    public static int fadeOutTicks = 0;
    public static final int MAX_FADE_OUT_TICKS = 40;
    public static boolean isFadingOut = false;
    @Nullable
    public static Identifier fadingOutMusicId = null;
    public static float fadingOutVolume = 1.0F;
    public static float currentFadeOutVolume = 1.0F; // 新增：记录淡出过程中的当前音量

    // 淡入相关
    public static int fadeInTicks = 0;
    public static final int MAX_FADE_IN_TICKS = 40;
    public static boolean isFadingIn = false;
    public static float fadeInStartVolume = 0.0F; // 从淡出时的当前音量开始
    public static float fadeInTargetVolume = 1.0F;

    // 重置所有状态
    public static void resetAll() {
        fadeOutTicks = 0;
        isFadingOut = false;
        fadingOutMusicId = null;
        fadingOutVolume = 1.0F;
        currentFadeOutVolume = 1.0F;

        fadeInTicks = 0;
        isFadingIn = false;
        fadeInStartVolume = 0.0F;
        fadeInTargetVolume = 1.0F;
    }

    // 开始淡出
    public static void startFadeOut(@Nullable Identifier currentMusicId, float currentVolume) {
        isFadingOut = true;
        fadeOutTicks = MAX_FADE_OUT_TICKS;
        fadingOutMusicId = currentMusicId;
        fadingOutVolume = currentVolume;
        currentFadeOutVolume = currentVolume; // 初始为当前音量

        // 取消淡入
        isFadingIn = false;
        fadeInTicks = 0;
    }

    // 更新淡出过程中的音量
    public static void updateFadeOutVolume(float volume) {
        currentFadeOutVolume = volume;
    }

    // 开始淡入恢复（从淡出时的当前音量开始）
    public static void startFadeIn(@Nullable Identifier musicId, float targetVolume) {
        isFadingIn = true;
        fadeInTicks = MAX_FADE_IN_TICKS;
        fadeInStartVolume = currentFadeOutVolume; // 从淡出时的当前音量开始
        fadeInTargetVolume = targetVolume;

        // 取消淡出
        isFadingOut = false;
        fadeOutTicks = 0;

        // 设置强制音乐
        forcedMusicId = musicId;
        forcedVolume = fadeInStartVolume; // 初始音量为淡出时的音量
    }
}