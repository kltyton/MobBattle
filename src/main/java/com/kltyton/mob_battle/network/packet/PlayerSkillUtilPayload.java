package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PlayerSkillUtilPayload(String name) implements CustomPayload {
    public static final CustomPayload.Id<PlayerSkillUtilPayload> ID = new CustomPayload.Id<>(Identifier.of(Mob_battle.MOD_ID, "player_skill_util"));
    public static final PacketCodec<RegistryByteBuf, PlayerSkillUtilPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, PlayerSkillUtilPayload::name,
            PlayerSkillUtilPayload::new
    );
    @Override public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
}
