package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ShieldSpawnPayload() implements CustomPayload {
    public static final CustomPayload.Id<ShieldSpawnPayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "shield_spawn"));
    public static final PacketCodec<RegistryByteBuf, ShieldSpawnPayload> CODEC = PacketCodec.unit(new ShieldSpawnPayload());
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}