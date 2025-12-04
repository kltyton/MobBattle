package com.kltyton.mob_battle.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class EnchantmentUtil {
    public static boolean hasEnchantment(World world, ItemStack itemStack, RegistryKey<Enchantment> enchantment) {
        DynamicRegistryManager registryManager = world.getRegistryManager();
        RegistryWrapper.Impl<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);
        return itemStack.getEnchantments().getLevel(enchantmentRegistry.getOrThrow(enchantment)) != 0;
    }
    public static boolean hasEnchantment(PlayerEntity player, ItemStack itemStack, RegistryKey<Enchantment> enchantment) {
        DynamicRegistryManager registryManager = player.getRegistryManager();
        RegistryWrapper.Impl<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);
        return itemStack.getEnchantments().getLevel(enchantmentRegistry.getOrThrow(enchantment)) != 0;
    }
    public static int getEnchantmentLevel(World world, ItemStack itemStack, RegistryKey<Enchantment> enchantment) {
        DynamicRegistryManager registryManager = world.getRegistryManager();
        RegistryWrapper.Impl<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);
        return itemStack.getEnchantments().getLevel(enchantmentRegistry.getOrThrow(enchantment));
    }
    public static int getEnchantmentLevel(PlayerEntity player, ItemStack itemStack, RegistryKey<Enchantment> enchantment) {
        DynamicRegistryManager registryManager = player.getRegistryManager();
        RegistryWrapper.Impl<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);
        return itemStack.getEnchantments().getLevel(enchantmentRegistry.getOrThrow(enchantment));
    }

    public static void addEnchantment(World world, ItemStack itemStack, RegistryKey<Enchantment> enchantment, int level) {
        DynamicRegistryManager registryManager = world.getRegistryManager();
        RegistryWrapper.Impl<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);
        itemStack.addEnchantment(new EnchantmentLevelEntry(enchantmentRegistry.getOrThrow(enchantment), level).enchantment(), level);
    }

    public static void addEnchantment(PlayerEntity player, ItemStack itemStack, RegistryKey<Enchantment> enchantment, int level) {
        DynamicRegistryManager registryManager = player.getRegistryManager();
        RegistryWrapper.Impl<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);
        itemStack.addEnchantment(new EnchantmentLevelEntry(enchantmentRegistry.getOrThrow(enchantment), level).enchantment(), level);
    }
}
