package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SoundPayload(String soundNmae, float volume) implements CustomPayload {
    public static final Id<SoundPayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "sound"));
    public static final PacketCodec<RegistryByteBuf, SoundPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SoundPayload::soundNmae,
            PacketCodecs.FLOAT, SoundPayload::volume,
            SoundPayload::new
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}