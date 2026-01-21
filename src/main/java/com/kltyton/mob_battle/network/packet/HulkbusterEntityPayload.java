package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public record HulkbusterEntityPayload(UUID uuid, Vec3d pos, String name) implements CustomPayload {
    public static final Id<HulkbusterEntityPayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "hulkbuster_entity"));
    public static final PacketCodec<RegistryByteBuf, HulkbusterEntityPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, HulkbusterEntityPayload::uuid,
            Vec3d.PACKET_CODEC, HulkbusterEntityPayload::pos,
            PacketCodecs.STRING, HulkbusterEntityPayload::name,
            HulkbusterEntityPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
