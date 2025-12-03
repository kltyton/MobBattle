package com.kltyton.mob_battle.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import com.kltyton.mob_battle.Mob_battle;

public record KeepInventoryPayload(int keeperId, boolean isKeep) implements CustomPayload {
    public static final Id<KeepInventoryPayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "keep_inventory"));
    public static final PacketCodec<RegistryByteBuf, KeepInventoryPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, KeepInventoryPayload::keeperId,
            PacketCodecs.BOOLEAN, KeepInventoryPayload::isKeep,
            KeepInventoryPayload::new
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
