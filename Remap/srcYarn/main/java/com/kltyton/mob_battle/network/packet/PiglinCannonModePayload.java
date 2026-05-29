package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PiglinCannonModePayload() implements CustomPayload {
    public static final CustomPayload.Id<PiglinCannonModePayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "piglin_cannon_mode"));
    public static final PacketCodec<RegistryByteBuf, PiglinCannonModePayload> CODEC = PacketCodec.unit(new PiglinCannonModePayload());
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
