package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SkillPayload(String skillName, int entityId) implements CustomPayload {
    public static final Id<SkillPayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "skill"));
    public static final PacketCodec<RegistryByteBuf, SkillPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SkillPayload::skillName,
            PacketCodecs.INTEGER, SkillPayload::entityId,
            SkillPayload::new
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
