package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ILeadUpdatePayload(int entityId, int iLead_1, int iLead_2) implements CustomPayload {
    public static final Id<ILeadUpdatePayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "lead_update_payload"));
    public static final PacketCodec<RegistryByteBuf, ILeadUpdatePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, ILeadUpdatePayload::entityId,
            PacketCodecs.VAR_INT, ILeadUpdatePayload::iLead_1,
            PacketCodecs.VAR_INT, ILeadUpdatePayload::iLead_2,
            ILeadUpdatePayload::new
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}