package com.kltyton.mob_battle.mixin.sound;

import com.kltyton.mob_battle.sounds.bgm.ClientBgmManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "getMusicInstance", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getWorld()Lnet/minecraft/world/World;"), cancellable = true)
    private void getMusicInstance(CallbackInfoReturnable<MusicInstance> cir) {
        // 优先处理淡入状态
        if (ClientBgmManager.isFadingIn && ClientBgmManager.forcedMusicId != null) {
            MusicSound sound = new MusicSound(RegistryEntry.of(SoundEvent.of(ClientBgmManager.forcedMusicId)), 40, 40, true);
            cir.setReturnValue(new MusicInstance(sound, ClientBgmManager.forcedVolume));
            cir.cancel();
        }
        // 处理淡出状态
        else if (ClientBgmManager.isFadingOut && ClientBgmManager.fadeOutTicks > 0 && ClientBgmManager.fadingOutMusicId != null) {
            MusicSound sound = new MusicSound(RegistryEntry.of(SoundEvent.of(ClientBgmManager.fadingOutMusicId)), 40, 40, true);
            float currentVolume = ClientBgmManager.fadingOutVolume * ((float)ClientBgmManager.fadeOutTicks / ClientBgmManager.MAX_FADE_OUT_TICKS);
            cir.setReturnValue(new MusicInstance(sound, currentVolume));
            cir.cancel();
        }
        // 处理正常强制播放
        else if (ClientBgmManager.forcedMusicId != null) {
            MusicSound sound = new MusicSound(RegistryEntry.of(SoundEvent.of(ClientBgmManager.forcedMusicId)), 40, 40, true);
            cir.setReturnValue(new MusicInstance(sound, ClientBgmManager.forcedVolume));
            cir.cancel();
        }
    }
}