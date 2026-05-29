package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record PiglinGeneralBonePayload(UUID uuid, Vec3 swordEnergyPos) implements CustomPacketPayload {
    public static final Type<PiglinGeneralBonePayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "piglin_general_bone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PiglinGeneralBonePayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, PiglinGeneralBonePayload::uuid,
            Vec3.STREAM_CODEC, PiglinGeneralBonePayload::swordEnergyPos,
            PiglinGeneralBonePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
