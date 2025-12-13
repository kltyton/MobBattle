package com.kltyton.mob_battle.network;

import com.kltyton.mob_battle.accessor.ILead;
import com.kltyton.mob_battle.items.itemgroup.ClientTagManager;
import com.kltyton.mob_battle.network.packet.EntityUniversalPayload;
import com.kltyton.mob_battle.network.packet.ILeadUpdatePayload;
import com.kltyton.mob_battle.network.packet.ItemGroupPayload;
import com.kltyton.mob_battle.network.packet.SoundPayload;
import com.kltyton.mob_battle.sounds.bgm.ClientBgmManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ClientPlayNetwork {
    public static void init() {
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
        ClientPlayNetworking.registerGlobalReceiver(EntityUniversalPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            World world = client.world;
            client.execute(() -> {
                // 客户端接收到包，更新实体数据
                if (world != null) {
                    Entity entity = world.getEntityById(payload.entityId());
                    if (entity instanceof ILead lead) {
                        lead.setIsUniversalLeadEnyity(payload.isUniversal());
                        lead.setIsInvisibleUniversalLeadEnyity(payload.isInvisible());
                    }
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(ItemGroupPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            boolean isOpen = payload.isOpen();
            client.execute(() -> ClientTagManager.isShen = isOpen);
        });
        ClientPlayNetworking.registerGlobalReceiver(ILeadUpdatePayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            Entity entity = client.world.getEntityById(payload.entityId());
            int iLead_1 = payload.iLead_1();
            int iLead_2 = payload.iLead_2();
            client.execute(() -> {
                if (entity != null) {
                    if (iLead_1 != 3) ((ILead) entity).setIsUniversalLeadEnyity(iLead_1 == 1);
                    if (iLead_2 != 3) ((ILead) entity).setIsInvisibleUniversalLeadEnyity(iLead_2 == 1);
                }
            });
        });
    }
}