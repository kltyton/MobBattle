package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CustomBossBarPayload(UUID bossBarUuid, ResourceLocation styleId, boolean visible) implements CustomPacketPayload {
    public static final Type<CustomBossBarPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "custom_boss_bar"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CustomBossBarPayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, CustomBossBarPayload::bossBarUuid,
            ResourceLocation.STREAM_CODEC, CustomBossBarPayload::styleId,
            ByteBufCodecs.BOOL, CustomBossBarPayload::visible,
            CustomBossBarPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
