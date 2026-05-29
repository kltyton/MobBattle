package com.kltyton.mob_battle.mixin.sound;

import com.kltyton.mob_battle.sounds.bgm.ClientBgmManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void getMusicInstance(CallbackInfoReturnable<MusicInfo> cir) {
        // 优先处理淡入状态
        if (ClientBgmManager.isFadingIn && ClientBgmManager.forcedMusicId != null) {
            Music sound = new Music(Holder.direct(SoundEvent.createVariableRangeEvent(ClientBgmManager.forcedMusicId)), 40, 40, true);
            cir.setReturnValue(new MusicInfo(sound, ClientBgmManager.forcedVolume));
            cir.cancel();
        }
        // 处理淡出状态
        else if (ClientBgmManager.isFadingOut && ClientBgmManager.fadeOutTicks > 0 && ClientBgmManager.fadingOutMusicId != null) {
            Music sound = new Music(Holder.direct(SoundEvent.createVariableRangeEvent(ClientBgmManager.fadingOutMusicId)), 40, 40, true);
            float currentVolume = ClientBgmManager.fadingOutVolume * ((float)ClientBgmManager.fadeOutTicks / ClientBgmManager.MAX_FADE_OUT_TICKS);
            cir.setReturnValue(new MusicInfo(sound, currentVolume));
            cir.cancel();
        }
        // 处理正常强制播放
        else if (ClientBgmManager.forcedMusicId != null) {
            Music sound = new Music(Holder.direct(SoundEvent.createVariableRangeEvent(ClientBgmManager.forcedMusicId)), 40, 40, true);
            cir.setReturnValue(new MusicInfo(sound, ClientBgmManager.forcedVolume));
            cir.cancel();
        }
    }
}
