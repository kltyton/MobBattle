package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record HulkbusterEntityPayload(UUID uuid, Vec3 pos, String name) implements CustomPacketPayload {
    public static final Type<HulkbusterEntityPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "hulkbuster_entity"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HulkbusterEntityPayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, HulkbusterEntityPayload::uuid,
            Vec3.STREAM_CODEC, HulkbusterEntityPayload::pos,
            ByteBufCodecs.STRING_UTF8, HulkbusterEntityPayload::name,
            HulkbusterEntityPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
