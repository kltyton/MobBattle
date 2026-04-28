package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record CustomBossBarPayload(UUID bossBarUuid, Identifier styleId, boolean visible) implements CustomPayload {
    public static final Id<CustomBossBarPayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "custom_boss_bar"));
    public static final PacketCodec<RegistryByteBuf, CustomBossBarPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, CustomBossBarPayload::bossBarUuid,
            Identifier.PACKET_CODEC, CustomBossBarPayload::styleId,
            PacketCodecs.BOOLEAN, CustomBossBarPayload::visible,
            CustomBossBarPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
