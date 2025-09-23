package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record HighbirdAngerPayload(int angerId) implements CustomPayload {
    public static final CustomPayload.Id<HighbirdAngerPayload> ID = new CustomPayload.Id<>(Identifier.of(Mob_battle.MOD_ID, "highbird_anger"));
    public static final PacketCodec<RegistryByteBuf, HighbirdAngerPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, HighbirdAngerPayload::angerId,
            HighbirdAngerPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
