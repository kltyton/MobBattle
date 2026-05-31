package com.kltyton.mob_battle.mixin.sound;

import com.kltyton.mob_battle.sounds.bgm.ClientBgmManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public abstract class MixinMusicTracker {
    @Shadow @Final private Minecraft minecraft;
    @Shadow private SoundInstance currentMusic;
    @Shadow private float currentGain;
    @Shadow public abstract void stopPlaying();

    @Unique private float targetVolume = 1.0F;
    @Unique private int localFadeInTicks = 0;
    @Unique private boolean fadingOut = false;

    @Unique
    private void mob_battle$applyMusicVolume(float volume) {
        this.currentGain = Mth.clamp(volume, 0.0F, 1.0F);
        this.minecraft.getSoundManager().updateCategoryVolume(SoundSource.MUSIC, this.currentGain);
    }

    // === 淡入淡出效果逻辑 ===
    @Inject(method = "tick", at = @At("TAIL"))
    private void mob_battle$applyFadeEffect(CallbackInfo ci) {
        if (this.currentMusic == null) return;

        // ==== 处理BGM系统的淡入 ====
        if (ClientBgmManager.isFadingIn && ClientBgmManager.fadeInTicks > 0) {
            ClientBgmManager.fadeInTicks--;

            // 计算淡入进度（从淡出时的音量线性增加到目标音量）
            float fadeProgress = 1.0f - (float) ClientBgmManager.fadeInTicks / ClientBgmManager.MAX_FADE_IN_TICKS;
            float volumeRange = ClientBgmManager.fadeInTargetVolume - ClientBgmManager.fadeInStartVolume;
            float currentVolume = ClientBgmManager.fadeInStartVolume + (volumeRange * fadeProgress);

            // 更新音量
            ClientBgmManager.forcedVolume = currentVolume;
            mob_battle$applyMusicVolume(currentVolume);

            // 淡入完成
            if (ClientBgmManager.fadeInTicks <= 0) {
                ClientBgmManager.isFadingIn = false;
                ClientBgmManager.forcedVolume = ClientBgmManager.fadeInTargetVolume;
                mob_battle$applyMusicVolume(ClientBgmManager.fadeInTargetVolume);
            }
            return;
        }

        // ==== 处理BGM系统的淡出 ====
        if (ClientBgmManager.isFadingOut && ClientBgmManager.fadeOutTicks > 0) {
            ClientBgmManager.fadeOutTicks--;

            // 计算淡出音量（从当前音量线性淡出到0）
            float fadeProgress = (float) ClientBgmManager.fadeOutTicks / ClientBgmManager.MAX_FADE_OUT_TICKS;
            float targetVolume = ClientBgmManager.fadingOutVolume * fadeProgress;

            // 更新淡出过程中的音量
            ClientBgmManager.updateFadeOutVolume(targetVolume);
            mob_battle$applyMusicVolume(targetVolume);


            // 如果淡出完成，停止音乐
            if (ClientBgmManager.fadeOutTicks <= 0) {
                mob_battle$applyMusicVolume(0.0F);
                this.minecraft.getSoundManager().stop(this.currentMusic);
                this.currentMusic = null;
                ClientBgmManager.resetAll();
                this.minecraft.getToastManager().hideNowPlayingToast();
            }
            return;
        }

        // ==== 原有的淡入逻辑（用于非BGM系统的音乐）====
        if (localFadeInTicks > 0) {
            this.currentGain = Math.min(targetVolume, this.currentGain + (targetVolume / localFadeInTicks));
            mob_battle$applyMusicVolume(this.currentGain);
            localFadeInTicks--;
        }

        // ==== 原有的淡出逻辑（用于非BGM系统的音乐）====
        if (fadingOut) {
            this.currentGain = Math.max(0.0F, this.currentGain - (targetVolume / 40));
            mob_battle$applyMusicVolume(this.currentGain);

            if (this.currentGain <= 0.01F) {
                mob_battle$applyMusicVolume(0.0F);
                this.minecraft.getSoundManager().stop(this.currentMusic);
                this.currentMusic = null;
                this.fadingOut = false;
                this.minecraft.getToastManager().hideNowPlayingToast();
            }
        }
    }

    // === 当音乐开始播放时 ===
    @Inject(method = "startPlaying", at = @At("TAIL"))
    private void mob_battle$onPlay(Music instance, CallbackInfo ci) {
        // 如果是从BGM淡出状态恢复，让BGM系统处理淡入
        if (ClientBgmManager.isFadingIn) {
            // BGM系统会处理淡入，这里不需要额外操作
            return;
        }

        // 正常的淡入逻辑
        this.localFadeInTicks = 40;
        this.targetVolume = ClientBgmManager.forcedVolume;
        mob_battle$applyMusicVolume(0.0F);
    }

    // === 当调用 stop() 时 ===
    @Inject(method = "stopPlaying()V", at = @At("HEAD"), cancellable = true)
    private void mob_battle$fadeOutInsteadOfImmediateStop(CallbackInfo ci) {
        if (this.currentMusic != null && !this.fadingOut &&
                !ClientBgmManager.isFadingOut && !ClientBgmManager.isFadingIn) {
            this.fadingOut = true;
            ci.cancel(); // 阻止原版立即停止音乐
        }
    }
}
