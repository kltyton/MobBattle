package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SoundPayload(String soundNmae, float volume) implements CustomPacketPayload {
    public static final Type<SoundPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "sound"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SoundPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SoundPayload::soundNmae,
            ByteBufCodecs.FLOAT, SoundPayload::volume,
            SoundPayload::new
    );
    @Override public Type<? extends CustomPacketPayload> type() { return ID; }
}