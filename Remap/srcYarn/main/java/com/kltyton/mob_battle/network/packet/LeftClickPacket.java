package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record LeftClickPacket(boolean pressing) implements CustomPayload {
    public static final Id<LeftClickPacket> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "left_click"));
    public static final PacketCodec<RegistryByteBuf, LeftClickPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, LeftClickPacket::pressing,
            LeftClickPacket::new
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
