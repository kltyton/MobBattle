package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PiglinCannonModePayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PiglinCannonModePayload> ID = new Type<>(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "piglin_cannon_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PiglinCannonModePayload> CODEC = StreamCodec.unit(new PiglinCannonModePayload());
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
