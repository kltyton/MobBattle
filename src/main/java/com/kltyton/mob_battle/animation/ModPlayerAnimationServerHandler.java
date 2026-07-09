package com.kltyton.mob_battle.animation;

import com.kltyton.mob_battle.network.packet.PlayerAnimationPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * 从服务器广播玩家动画。
 * <p>
 * 客户端各自根据玩家实体 ID 找到目标实体并播放，因此目标玩家和旁观玩家都能看到动画。
 */
public final class ModPlayerAnimationServerHandler {
    // 停止包不需要真实动画 ID，但网络字段不能为空，因此使用占位 ID。
    private static final Identifier EMPTY_ANIMATION = Identifier.withDefaultNamespace("empty");

    /**
     * 播放动画并同步给与目标玩家处于同一维度的所有玩家。
     */
    public static void play(ServerPlayer animatedPlayer, Identifier animationId) {
        send(animatedPlayer, animationId, false);
    }

    /**
     * 停止动画并同步给与目标玩家处于同一维度的所有玩家。
     */
    public static void stop(ServerPlayer animatedPlayer) {
        send(animatedPlayer, EMPTY_ANIMATION, true);
    }

    private static void send(ServerPlayer animatedPlayer, Identifier animationId, boolean stop) {
        MinecraftServer server = animatedPlayer.level().getServer();
        if (server == null) {
            return;
        }

        PlayerAnimationPayload payload = new PlayerAnimationPayload(animatedPlayer.getId(), animationId, stop);
        for (ServerPlayer viewer : server.getPlayerList().getPlayers()) {
            // 实体 ID 只在当前维度内有效，同时跳过不支持该自定义包的客户端。
            if (viewer.level() == animatedPlayer.level()
                    && ServerPlayNetworking.canSend(viewer, PlayerAnimationPayload.ID)) {
                ServerPlayNetworking.send(viewer, payload);
            }
        }
    }
}

