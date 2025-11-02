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
        // 如果正在淡出但还有时间，继续播放（音量会逐渐降低）
        if (ClientBgmManager.isFadingOut && ClientBgmManager.fadeOutTicks > 0) {
            MusicSound sound = new MusicSound(RegistryEntry.of(SoundEvent.of(ClientBgmManager.fadingOutMusicId)), 40, 40, true);
            cir.setReturnValue(new MusicInstance(sound, ClientBgmManager.fadingOutVolume));
            cir.cancel();
        }
        // 如果有强制播放的音乐
        else if (ClientBgmManager.forcedMusicId != null) {
            MusicSound sound = new MusicSound(RegistryEntry.of(SoundEvent.of(ClientBgmManager.forcedMusicId)), 40, 40, true);
            cir.setReturnValue(new MusicInstance(sound, ClientBgmManager.forcedVolume));
            cir.cancel();
        }
    }
}