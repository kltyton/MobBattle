package com.kltyton.mob_battle.network.receiver.client;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.network.packet.SoundPayload;
import com.kltyton.mob_battle.sounds.bgm.ClientBgmManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.IdentifierException;
import net.minecraft.resources.Identifier;

/**
 * 客户端音频（BGM）接收器。
 *
 * <p>负责处理服务器下发的 {@link SoundPayload}：支持 “fade_out” 淡出指令与正常音乐播放指令。
 * Fabric 当前回调已经位于客户端渲染线程，BGM 状态直接更新以避免重复排队。</p>
 */
public final class ClientSoundReceiver {

    private ClientSoundReceiver() {
    }

    /**
     * 注册音频接收器（SoundPayload）。
     */
    public static void registerSound() {
        ClientPlayNetworking.registerGlobalReceiver(SoundPayload.ID, (payload, context) -> {
            String soundName = payload.soundNmae();
            float volume = payload.volume();
            if ("fade_out".equals(soundName)) {
                    // 收到淡出指令
                    if (ClientBgmManager.forcedMusicId != null && !ClientBgmManager.isFadingOut) {
                        ClientBgmManager.startFadeOut(ClientBgmManager.forcedMusicId, ClientBgmManager.forcedVolume);
                    }
                    ClientBgmManager.forcedMusicId = null;
                    ClientBgmManager.forcedVolume = 0f;
            } else {
                    // 收到正常播放指令
                    Identifier id;
                    try {
                        id = Identifier.parse(soundName);
                    } catch (IdentifierException e) {
                        // 拒绝格式非法的音乐资源 ID，仅记录警告，避免渲染线程崩溃
                        Mob_battle.LOGGER.warn("收到格式非法的音乐资源 ID，已忽略：{}", soundName, e);
                        return;
                    }

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
    }
}
