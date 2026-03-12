package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ZiJinPayload() implements CustomPayload {
    public static final CustomPayload.Id<ZiJinPayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "zi_jin"));
    public static final PacketCodec<RegistryByteBuf, ZiJinPayload> CODEC = PacketCodec.unit(new ZiJinPayload());
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
