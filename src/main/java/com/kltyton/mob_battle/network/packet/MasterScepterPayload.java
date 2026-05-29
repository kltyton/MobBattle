package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MasterScepterPayload(String id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MasterScepterPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "master_scepter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MasterScepterPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, MasterScepterPayload::id,
            MasterScepterPayload::new
    );
    @Override public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return ID; }
}
