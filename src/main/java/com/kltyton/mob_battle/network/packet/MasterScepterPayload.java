package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MasterScepterPayload(String id) implements CustomPayload {
    public static final CustomPayload.Id<MasterScepterPayload> ID = new CustomPayload.Id<>(Identifier.of(Mob_battle.MOD_ID, "master_scepter"));
    public static final PacketCodec<RegistryByteBuf, MasterScepterPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, MasterScepterPayload::id,
            MasterScepterPayload::new
    );
    @Override public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
}
