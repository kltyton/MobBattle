package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ShieldSpawnPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ShieldSpawnPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "shield_spawn"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShieldSpawnPayload> CODEC = StreamCodec.unit(new ShieldSpawnPayload());
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}