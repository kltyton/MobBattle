package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PermissionPayload(boolean isWhitelisted) implements CustomPayload {
    public static final Id<PermissionPayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "permission"));
    public static final PacketCodec<RegistryByteBuf, PermissionPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, PermissionPayload::isWhitelisted,
            PermissionPayload::new
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
