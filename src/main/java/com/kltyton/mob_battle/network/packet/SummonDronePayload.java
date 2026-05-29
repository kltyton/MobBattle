package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SummonDronePayload(int mode) implements CustomPacketPayload {
    public static final Type<SummonDronePayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "summon_drone"));
    //public static final PacketCodec<RegistryByteBuf, SummonDronePayload> CODEC = PacketCodec.unit(new SummonDronePayload());
    public static final StreamCodec<RegistryFriendlyByteBuf, SummonDronePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SummonDronePayload::mode,
            SummonDronePayload::new
    );
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}

