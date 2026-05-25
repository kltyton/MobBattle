package com.kltyton.mob_battle.config.whitelist;

import net.minecraft.server.network.ServerPlayerEntity;

public final class MobBattlePermissions {

    public static boolean canUseProtectedContent(ServerPlayerEntity player) {
        //return true;
        if (player == null) return false;
        // 纯白名单模式
        return ModPlayerWhitelist.isWhitelisted(player);

        // OP也拥有权限
        // return player.hasPermissionLevel(2) || ModPlayerWhitelist.isWhitelisted(player);
    }
}

