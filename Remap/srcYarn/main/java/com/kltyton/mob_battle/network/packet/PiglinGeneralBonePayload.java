package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public record PiglinGeneralBonePayload(UUID uuid, Vec3d swordEnergyPos) implements CustomPayload {
    public static final Id<PiglinGeneralBonePayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "piglin_general_bone"));
    public static final PacketCodec<RegistryByteBuf, PiglinGeneralBonePayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, PiglinGeneralBonePayload::uuid,
            Vec3d.PACKET_CODEC, PiglinGeneralBonePayload::swordEnergyPos,
            PiglinGeneralBonePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
