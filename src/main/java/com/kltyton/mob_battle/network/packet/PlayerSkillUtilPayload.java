package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PlayerSkillUtilPayload(String name) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerSkillUtilPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "player_skill_util"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSkillUtilPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PlayerSkillUtilPayload::name,
            PlayerSkillUtilPayload::new
    );
    @Override public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return ID; }
}
