package com.kltyton.mob_battle.event.player;

import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import com.kltyton.mob_battle.network.packet.PermissionPayload;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class PermissionEvents {
    public static void init() {
        // 监听玩家加入服务器事件
        ServerPlayerEvents.JOIN.register(( player) -> {
            // 获取玩家权限状态
            boolean isWhitelisted = MobBattlePermissions.canUseProtectedContent(player);
            ServerPlayNetworking.send(player, new PermissionPayload(isWhitelisted));
        });
    }
}
