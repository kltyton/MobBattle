package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SkillPayload(String skillName, int entityId) implements CustomPacketPayload {
    public static final Type<SkillPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "skill"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SkillPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SkillPayload::skillName,
            ByteBufCodecs.VAR_INT, SkillPayload::entityId,
            SkillPayload::new
    );
    @Override public Type<? extends CustomPacketPayload> type() { return ID; }
}
