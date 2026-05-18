package com.kltyton.mob_battle.config.whitelist;

import net.minecraft.server.network.ServerPlayerEntity;

public final class MobBattlePermissions {

    private MobBattlePermissions() {}

    public static boolean canUseProtectedContent(ServerPlayerEntity player) {

        if (player == null) return false;
        //debug 模式: return true;
        // 纯白名单模式
        return ModPlayerWhitelist.isWhitelisted(player);

        // 如果你想让 OP 也拥有权限，就改成：
        // return player.hasPermissionLevel(2) || ModPlayerWhitelist.isWhitelisted(player);
    }
}

