package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SummonDronePayload(int type) implements CustomPayload {
    public static final Id<SummonDronePayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "summon_drone"));
    //public static final PacketCodec<RegistryByteBuf, SummonDronePayload> CODEC = PacketCodec.unit(new SummonDronePayload());
    public static final PacketCodec<RegistryByteBuf, SummonDronePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, SummonDronePayload::type,
            SummonDronePayload::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

