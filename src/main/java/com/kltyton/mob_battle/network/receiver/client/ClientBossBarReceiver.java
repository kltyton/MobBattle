package com.kltyton.mob_battle.network.receiver.client;

import com.kltyton.mob_battle.bossbar.CustomBossBarClientState;
import com.kltyton.mob_battle.network.packet.CustomBossBarPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * 客户端自定义 Boss 栏接收器。
 *
 * <p>负责处理自定义 Boss 栏状态包 {@link CustomBossBarPayload}：可见时写入或更新 Boss 栏状态，
 * 不可见时移除对应 Boss 栏。Fabric 当前回调已位于客户端渲染线程，
 * 因此直接更新状态，避免重复调度。</p>
 */
public final class ClientBossBarReceiver {

    private ClientBossBarReceiver() {
    }

    /**
     * 注册自定义 Boss 栏接收器（CustomBossBarPayload）。
     */
    public static void registerCustomBossBar() {
        ClientPlayNetworking.registerGlobalReceiver(CustomBossBarPayload.ID, (payload, context) -> {
            if (payload.visible()) {
                CustomBossBarClientState.set(payload.bossBarUuid(), payload.styleId());
            } else {
                CustomBossBarClientState.remove(payload.bossBarUuid());
            }
        });
    }
}
