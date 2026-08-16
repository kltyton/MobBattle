package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PalMorePlayerAnimationPayload(int avatarEntityId, Identifier animationId)
        implements CustomPacketPayload {
    public static final Type<PalMorePlayerAnimationPayload> ID =
            new Type<>(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "pal_more_player_animation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PalMorePlayerAnimationPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PalMorePlayerAnimationPayload::avatarEntityId,
            Identifier.STREAM_CODEC, PalMorePlayerAnimationPayload::animationId,
            PalMorePlayerAnimationPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
