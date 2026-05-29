package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public record EnchantmentPayload(ItemStack itemStack, RegistryKey<Enchantment> enchantment, int level) implements CustomPayload {
    public static final CustomPayload.Id<EnchantmentPayload> ID = new CustomPayload.Id<>(Identifier.of(Mob_battle.MOD_ID, "enchantment"));
    public static final PacketCodec<RegistryByteBuf, EnchantmentPayload> CODEC = PacketCodec.tuple(
            ItemStack.PACKET_CODEC, EnchantmentPayload::itemStack,
            RegistryKey.createPacketCodec(RegistryKeys.ENCHANTMENT), EnchantmentPayload::enchantment,
            PacketCodecs.VAR_INT, EnchantmentPayload::level,
            EnchantmentPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}