package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PermissionPayload(boolean isWhitelisted) implements CustomPacketPayload {
    public static final Type<PermissionPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "permission"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PermissionPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PermissionPayload::isWhitelisted,
            PermissionPayload::new
    );
    @Override public Type<? extends CustomPacketPayload> type() { return ID; }
}
