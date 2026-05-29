package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;


public record HighbirdAttackPayload(int attackerId) implements CustomPayload {
    public static final Id<HighbirdAttackPayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "highbird_attack"));
    public static final PacketCodec<RegistryByteBuf, HighbirdAttackPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, HighbirdAttackPayload::attackerId,
            HighbirdAttackPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
