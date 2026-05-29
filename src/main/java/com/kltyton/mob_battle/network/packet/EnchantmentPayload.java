package com.kltyton.mob_battle.network.packet;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public record EnchantmentPayload(ItemStack itemStack, ResourceKey<Enchantment> enchantment, int level) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<EnchantmentPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "enchantment"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentPayload> CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, EnchantmentPayload::itemStack,
            ResourceKey.streamCodec(Registries.ENCHANTMENT), EnchantmentPayload::enchantment,
            ByteBufCodecs.VAR_INT, EnchantmentPayload::level,
            EnchantmentPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}