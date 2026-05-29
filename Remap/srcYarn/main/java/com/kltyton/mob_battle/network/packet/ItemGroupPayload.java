package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ItemGroupPayload(boolean isOpen) implements CustomPayload {
    public static final Id<ItemGroupPayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "item_group_payload"));
    public static final PacketCodec<RegistryByteBuf, ItemGroupPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, ItemGroupPayload::isOpen,
            ItemGroupPayload::new
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
