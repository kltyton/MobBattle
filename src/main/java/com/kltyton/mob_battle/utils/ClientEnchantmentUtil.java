package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.network.packet.EnchantmentPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import software.bernie.geckolib.util.ClientUtil;

@Environment(EnvType.CLIENT)
public class ClientEnchantmentUtil {
    public static boolean hasEnchantment(ItemStack itemStack, RegistryKey<Enchantment> enchantment) {
        return EnchantmentUtil.hasEnchantment(ClientUtil.getLevel(), itemStack, enchantment);
    }
    public static int getEnchantmentLevel(ItemStack itemStack, RegistryKey<Enchantment> enchantment) {
        return EnchantmentUtil.getEnchantmentLevel(ClientUtil.getLevel(), itemStack, enchantment);
    }
    public static void addEnchantment(ItemStack itemStack, RegistryKey<Enchantment> enchantment, int level) {
        EnchantmentUtil.addEnchantment(ClientUtil.getLevel(), itemStack, enchantment, level);
        ClientPlayNetworking.send(new EnchantmentPayload(itemStack, enchantment, level));
    }
}
