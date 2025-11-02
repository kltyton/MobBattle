package com.kltyton.mob_battle.mixin.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicInstance;
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
    @Unique private int fadeOutTicks = 0;
    @Unique private boolean fadingOut = false;

    // === 淡入淡出效果逻辑 ===
    @Inject(method = "tick", at = @At("TAIL"))
    private void mob_battle$applyFadeEffect(CallbackInfo ci) {
        if (this.current == null) return;

        // ==== 淡入 ====
        if (fadeInTicks > 0) {
            this.volume = Math.min(targetVolume, this.volume + (targetVolume / fadeInTicks));
            this.client.getSoundManager().setVolume(this.current, this.volume);
            fadeInTicks--;
        }

        // ==== 淡出 ====
        if (fadingOut) {

            this.volume = Math.max(0.0F, this.volume - (targetVolume / fadeOutTicks));
            this.client.getSoundManager().setVolume(this.current, this.volume);
            fadeOutTicks--;

            if (fadeOutTicks <= 0 || this.volume <= 0.01F) {
                this.client.getSoundManager().stop(this.current);
                this.current = null;
                this.fadingOut = false;
                this.client.getToastManager().onMusicTrackStop();
            }
        }
    }

    // === 当音乐开始播放时 ===
    @Inject(method = "play", at = @At("TAIL"))
    private void mob_battle$onPlay(MusicInstance instance, CallbackInfo ci) {
        this.fadeInTicks = 40; // 约2秒淡入
        this.targetVolume = instance.volume();
        this.volume = 0.0F;
        this.client.getSoundManager().setVolume(this.current, this.volume);
    }

    // === 当调用 stop() 时 ===
    @Inject(method = "stop()V", at = @At("HEAD"), cancellable = true)
    private void mob_battle$fadeOutInsteadOfImmediateStop(CallbackInfo ci) {
        if (this.current != null && !this.fadingOut) {
            this.fadingOut = true;
            this.fadeOutTicks = 40; // 约2秒淡出
            ci.cancel(); // 阻止原版立即停止音乐
        }
    }
}
