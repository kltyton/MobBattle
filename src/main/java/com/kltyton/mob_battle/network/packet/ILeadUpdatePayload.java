package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ILeadUpdatePayload(int entityId, int iLead_1, int iLead_2) implements CustomPacketPayload {
    public static final Type<ILeadUpdatePayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "lead_update_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ILeadUpdatePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ILeadUpdatePayload::entityId,
            ByteBufCodecs.VAR_INT, ILeadUpdatePayload::iLead_1,
            ByteBufCodecs.VAR_INT, ILeadUpdatePayload::iLead_2,
            ILeadUpdatePayload::new
    );
    @Override public Type<? extends CustomPacketPayload> type() { return ID; }
}