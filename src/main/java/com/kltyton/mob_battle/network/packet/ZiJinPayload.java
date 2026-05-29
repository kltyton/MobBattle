package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ZiJinPayload(int skill_id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ZiJinPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "zi_jin"));
    //public static final PacketCodec<RegistryByteBuf, ZiJinPayload> CODEC = PacketCodec.unit(new ZiJinPayload());
    public static final StreamCodec<RegistryFriendlyByteBuf, ZiJinPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ZiJinPayload::skill_id,
            ZiJinPayload::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
