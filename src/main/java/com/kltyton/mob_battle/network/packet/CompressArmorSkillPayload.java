package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CompressArmorSkillPayload(int skill_id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CompressArmorSkillPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "compress_armor_skill"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CompressArmorSkillPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CompressArmorSkillPayload::skill_id,
            CompressArmorSkillPayload::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
