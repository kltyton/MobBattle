package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record CompressArmorSkillPayload(int skill_id) implements CustomPayload {
    public static final CustomPayload.Id<CompressArmorSkillPayload> ID = new Id<>(Identifier.of(Mob_battle.MOD_ID, "compress_armor_skill"));
    public static final PacketCodec<RegistryByteBuf, CompressArmorSkillPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, CompressArmorSkillPayload::skill_id,
            CompressArmorSkillPayload::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
