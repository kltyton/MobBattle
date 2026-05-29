package com.kltyton.mob_battle.mixin.sound;

import com.kltyton.mob_battle.sounds.bgm.ClientBgmManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicTracker.class)
public abstract class MixinMusicTracker {
    @Shadow @Final private MinecraftClient client;
    @Shadow private SoundInstance current;
    @Shadow private float volume;
    @Shadow public abstract void stop();

    @Unique private float targetVolume = 1.0F;
    @Unique private int localFadeInTicks = 0;
    @Unique private boolean fadingOut = false;

    // === 淡入淡出效果逻辑 ===
    @Inject(method = "tick", at = @At("TAIL"))
    private void mob_battle$applyFadeEffect(CallbackInfo ci) {
        if (this.current == null) return;

        // ==== 处理BGM系统的淡入 ====
        if (ClientBgmManager.isFadingIn && ClientBgmManager.fadeInTicks > 0) {
            ClientBgmManager.fadeInTicks--;

            // 计算淡入进度（从淡出时的音量线性增加到目标音量）
            float fadeProgress = 1.0f - (float) ClientBgmManager.fadeInTicks / ClientBgmManager.MAX_FADE_IN_TICKS;
            float volumeRange = ClientBgmManager.fadeInTargetVolume - ClientBgmManager.fadeInStartVolume;
            float currentVolume = ClientBgmManager.fadeInStartVolume + (volumeRange * fadeProgress);

            // 更新音量
            ClientBgmManager.forcedVolume = currentVolume;
            this.client.getSoundManager().setVolume(this.current, currentVolume);

            // 淡入完成
            if (ClientBgmManager.fadeInTicks <= 0) {
                ClientBgmManager.isFadingIn = false;
                ClientBgmManager.forcedVolume = ClientBgmManager.fadeInTargetVolume;
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

            this.client.getSoundManager().setVolume(this.current, targetVolume);

            // 如果淡出完成，停止音乐
            if (ClientBgmManager.fadeOutTicks <= 0) {
                this.client.getSoundManager().stop(this.current);
                this.current = null;
                ClientBgmManager.resetAll();
                this.client.getToastManager().onMusicTrackStop();
            }
            return;
        }

        // ==== 原有的淡入逻辑（用于非BGM系统的音乐）====
        if (localFadeInTicks > 0) {
            this.volume = Math.min(targetVolume, this.volume + (targetVolume / localFadeInTicks));
            this.client.getSoundManager().setVolume(this.current, this.volume);
            localFadeInTicks--;
        }

        // ==== 原有的淡出逻辑（用于非BGM系统的音乐）====
        if (fadingOut) {
            this.volume = Math.max(0.0F, this.volume - (targetVolume / 40));
            this.client.getSoundManager().setVolume(this.current, this.volume);

            if (this.volume <= 0.01F) {
                this.client.getSoundManager().stop(this.current);
                this.current = null;
                this.fadingOut = false;
                this.client.getToastManager().onMusicTrackStop();
            }
        }
    }

    // === 当音乐开始播放时 ===
    @Inject(method = "play", at = @At("TAIL"))
    private void mob_battle$onPlay(net.minecraft.client.sound.MusicInstance instance, CallbackInfo ci) {
        // 如果是从BGM淡出状态恢复，让BGM系统处理淡入
        if (ClientBgmManager.isFadingIn) {
            // BGM系统会处理淡入，这里不需要额外操作
            return;
        }

        // 正常的淡入逻辑
        this.localFadeInTicks = 40;
        this.targetVolume = instance.volume();
        this.volume = 0.0F;
        this.client.getSoundManager().setVolume(this.current, this.volume);
    }

    // === 当调用 stop() 时 ===
    @Inject(method = "stop()V", at = @At("HEAD"), cancellable = true)
    private void mob_battle$fadeOutInsteadOfImmediateStop(CallbackInfo ci) {
        if (this.current != null && !this.fadingOut &&
                !ClientBgmManager.isFadingOut && !ClientBgmManager.isFadingIn) {
            this.fadingOut = true;
            ci.cancel(); // 阻止原版立即停止音乐
        }
    }
}