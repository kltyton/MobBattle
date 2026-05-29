package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.network.packet.EnchantmentPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import software.bernie.geckolib.util.ClientUtil;

@Environment(EnvType.CLIENT)
public class ClientEnchantmentUtil {
    public static boolean hasEnchantment(ItemStack itemStack, ResourceKey<Enchantment> enchantment) {
        return EnchantmentUtil.hasEnchantment(ClientUtil.getLevel(), itemStack, enchantment);
    }
    public static int getEnchantmentLevel(ItemStack itemStack, ResourceKey<Enchantment> enchantment) {
        return EnchantmentUtil.getEnchantmentLevel(ClientUtil.getLevel(), itemStack, enchantment);
    }
    public static void addEnchantment(ItemStack itemStack, ResourceKey<Enchantment> enchantment, int level) {
        EnchantmentUtil.addEnchantment(ClientUtil.getLevel(), itemStack, enchantment, level);
        ClientPlayNetworking.send(new EnchantmentPayload(itemStack, enchantment, level));
    }
}
