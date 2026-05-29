package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record LeftClickPacket(boolean pressing) implements CustomPacketPayload {
    public static final Type<LeftClickPacket> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "left_click"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LeftClickPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, LeftClickPacket::pressing,
            LeftClickPacket::new
    );
    @Override public Type<? extends CustomPacketPayload> type() { return ID; }
}
