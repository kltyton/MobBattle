package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PlayerSkillPayload(String skillName, int entityId) implements CustomPacketPayload {
    public static final Type<PlayerSkillPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "player_skill"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSkillPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PlayerSkillPayload::skillName,
            ByteBufCodecs.VAR_INT, PlayerSkillPayload::entityId,
            PlayerSkillPayload::new
    );
    @Override public Type<? extends CustomPacketPayload> type() { return ID; }
}
