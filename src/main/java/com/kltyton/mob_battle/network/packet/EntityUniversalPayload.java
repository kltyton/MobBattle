package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record EntityUniversalPayload(int entityId, boolean isUniversal, boolean isInvisible) implements CustomPayload {
    public static final CustomPayload.Id<EntityUniversalPayload> ID = new CustomPayload.Id<>(Identifier.of(Mob_battle.MOD_ID, "sync_universal"));
    public static final PacketCodec<RegistryByteBuf, EntityUniversalPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, EntityUniversalPayload::entityId,
            PacketCodecs.BOOLEAN, EntityUniversalPayload::isUniversal,
            PacketCodecs.BOOLEAN, EntityUniversalPayload::isInvisible,
            EntityUniversalPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
