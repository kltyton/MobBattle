package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public record ParticleStormEmitterPayload(Identifier particleId, Vec3 pos, int entityId)
        implements CustomPacketPayload {
    public static final Type<ParticleStormEmitterPayload> ID =
            new Type<>(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "particle_storm_emitter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleStormEmitterPayload> CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, ParticleStormEmitterPayload::particleId,
            Vec3.STREAM_CODEC, ParticleStormEmitterPayload::pos,
            ByteBufCodecs.VAR_INT, ParticleStormEmitterPayload::entityId,
            ParticleStormEmitterPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
