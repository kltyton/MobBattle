package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record HighbirdAttackPayload(int attackerId) implements CustomPacketPayload {
    public static final Type<HighbirdAttackPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_attack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HighbirdAttackPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, HighbirdAttackPayload::attackerId,
            HighbirdAttackPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
