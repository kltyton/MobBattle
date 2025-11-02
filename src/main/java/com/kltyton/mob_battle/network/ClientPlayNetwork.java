package com.kltyton.mob_battle.network;

import com.kltyton.mob_battle.network.packet.SoundPayload;
import com.kltyton.mob_battle.sounds.bgm.ClientBgmManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class ClientPlayNetwork {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(
                SoundPayload.ID,
                SoundPayload.CODEC
        );
        ClientPlayNetworking.registerGlobalReceiver(SoundPayload.ID, (payload, context)  -> {
            String soundName = payload.soundNmae();
            float volume = payload.volume();
            MinecraftClient client = context.client();
            client.execute(() -> {
                if ("fade_out".equals(soundName)) {
                    // 收到淡出指令
                    if (ClientBgmManager.forcedMusicId != null && !ClientBgmManager.isFadingOut) {
                        ClientBgmManager.startFadeOut(ClientBgmManager.forcedMusicId, ClientBgmManager.forcedVolume);
                    }
                    ClientBgmManager.forcedMusicId = null;
                    ClientBgmManager.forcedVolume = 0f;
                } else {
                    // 收到正常播放指令
                    Identifier id = Identifier.of(soundName);

                    // 如果正在淡出且是同一首音乐，则开始淡入恢复
                    if (ClientBgmManager.isFadingOut &&
                            ClientBgmManager.fadingOutMusicId != null &&
                            ClientBgmManager.fadingOutMusicId.equals(id)) {
                        ClientBgmManager.startFadeIn(id, volume);
                    } else {
                        // 全新播放或切换音乐
                        ClientBgmManager.resetAll();
                        ClientBgmManager.forcedMusicId = id;
                        ClientBgmManager.forcedVolume = volume;
                    }
                }
            });
        });
    }
}