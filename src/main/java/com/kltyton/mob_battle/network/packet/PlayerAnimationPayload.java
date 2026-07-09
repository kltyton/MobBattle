package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * 玩家动画客户端同步包。
 *
 * @param avatarEntityId 目标玩家在当前维度中的实体 ID
 * @param animationId    要播放的玩家动画资源 ID
 * @param stop           true 时忽略动画 ID 并停止当前动画
 */
public record PlayerAnimationPayload(int avatarEntityId, Identifier animationId, boolean stop)
        implements CustomPacketPayload {
    public static final Type<PlayerAnimationPayload> ID =
            new Type<>(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "player_animation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerAnimationPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PlayerAnimationPayload::avatarEntityId,
            Identifier.STREAM_CODEC, PlayerAnimationPayload::animationId,
            ByteBufCodecs.BOOL, PlayerAnimationPayload::stop,
            PlayerAnimationPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
