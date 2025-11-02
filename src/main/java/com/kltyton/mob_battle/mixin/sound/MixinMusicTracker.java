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
    @Unique private int fadeInTicks = 0;
    @Unique private boolean fadingOut = false;

    // === 淡入淡出效果逻辑 ===
    @Inject(method = "tick", at = @At("TAIL"))
    private void mob_battle$applyFadeEffect(CallbackInfo ci) {
        if (this.current == null) return;

        // ==== 处理BGM系统的淡出 ====
        if (ClientBgmManager.isFadingOut) {
            ClientBgmManager.fadeOutTicks--;

            // 计算淡出音量（从当前音量线性淡出到0）
            float fadeProgress = (float) ClientBgmManager.fadeOutTicks / ClientBgmManager.MAX_FADE_OUT_TICKS;
            float targetVolume = this.volume * fadeProgress;

            this.client.getSoundManager().setVolume(this.current, targetVolume);

            // 如果淡出完成，停止音乐
            if (ClientBgmManager.fadeOutTicks <= 0) {
                this.client.getSoundManager().stop(this.current);
                this.current = null;
                ClientBgmManager.resetFadeOut();
                this.client.getToastManager().onMusicTrackStop();
            }
            return;
        }

        // ==== 原有的淡入逻辑 ====
        if (fadeInTicks > 0) {
            this.volume = Math.min(targetVolume, this.volume + (targetVolume / fadeInTicks));
            this.client.getSoundManager().setVolume(this.current, this.volume);
            fadeInTicks--;
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
        // 如果正在从BGM淡出状态恢复，使用BGM的音量设置
        if (ClientBgmManager.isFadingOut) {
            this.fadeInTicks = 40;
            this.targetVolume = ClientBgmManager.forcedVolume;
            this.volume = 0.0F;
        } else {
            this.fadeInTicks = 40;
            this.targetVolume = instance.volume();
            this.volume = 0.0F;
        }
        this.client.getSoundManager().setVolume(this.current, this.volume);
    }

    // === 当调用 stop() 时 ===
    @Inject(method = "stop()V", at = @At("HEAD"), cancellable = true)
    private void mob_battle$fadeOutInsteadOfImmediateStop(CallbackInfo ci) {
        if (this.current != null && !this.fadingOut && !ClientBgmManager.isFadingOut) {
            this.fadingOut = true;
            ci.cancel(); // 阻止原版立即停止音乐
        }
    }
}