package com.kltyton.mob_battle.network.receiver.client;

import com.kltyton.mob_battle.config.whitelist.ClientPermissionState;
import com.kltyton.mob_battle.items.itemgroup.ClientItemGroupState;
import com.kltyton.mob_battle.network.packet.ItemGroupPayload;
import com.kltyton.mob_battle.network.packet.PermissionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * 客户端状态同步接收器。
 *
 * <p>负责处理物品栏组状态包 {@link ItemGroupPayload} 与权限状态包 {@link PermissionPayload}。
 * Fabric 当前 Payload 回调已经位于客户端渲染线程，因此直接更新客户端状态，
 * 避免重复排队引入额外一帧延迟。</p>
 */
public final class ClientStateSyncReceivers {

    private ClientStateSyncReceivers() {
    }

    /**
     * 注册物品栏组状态接收器（ItemGroupPayload）。
     */
    public static void registerItemGroup() {
        ClientPlayNetworking.registerGlobalReceiver(ItemGroupPayload.ID,
                (payload, context) -> ClientItemGroupState.isOpen = payload.isOpen());
    }

    /**
     * 注册权限状态接收器（PermissionPayload）。
     */
    public static void registerPermission() {
        ClientPlayNetworking.registerGlobalReceiver(PermissionPayload.ID,
                (payload, context) -> ClientPermissionState.setWhitelisted(payload.isWhitelisted()));
    }
}
