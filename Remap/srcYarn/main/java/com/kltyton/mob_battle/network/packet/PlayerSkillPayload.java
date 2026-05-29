package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PlayerSkillPayload(String skillName, int entityId) implements CustomPayload {
    public static final Id<PlayerSkillPayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "player_skill"));
    public static final PacketCodec<RegistryByteBuf, PlayerSkillPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, PlayerSkillPayload::skillName,
            PacketCodecs.VAR_INT, PlayerSkillPayload::entityId,
            PlayerSkillPayload::new
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
