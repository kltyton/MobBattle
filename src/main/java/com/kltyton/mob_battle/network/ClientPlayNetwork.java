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
            Identifier id = Identifier.of(payload.soundNmae());
            float volume = payload.volume();
            MinecraftClient client = context.client();
            client.execute(() -> {
                ClientBgmManager.forcedMusicId = id;
                ClientBgmManager.forcedVolume = volume;
            });
        });
    }
}
