package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ItemGroupPayload(boolean isOpen) implements CustomPacketPayload {
    public static final Type<ItemGroupPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "item_group_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemGroupPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ItemGroupPayload::isOpen,
            ItemGroupPayload::new
    );
    @Override public Type<? extends CustomPacketPayload> type() { return ID; }
}
