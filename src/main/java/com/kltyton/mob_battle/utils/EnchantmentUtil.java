package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

public class EnchantmentUtil {
    public static boolean hasEnchantment(Level world, ItemStack itemStack, ResourceKey<Enchantment> enchantment) {
        RegistryAccess registryManager = world.registryAccess();
        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = registryManager.lookupOrThrow(Registries.ENCHANTMENT);
        return itemStack.getEnchantments().getLevel(enchantmentRegistry.getOrThrow(enchantment)) != 0;
    }

    public static boolean hasEnchantment(ItemStack itemStack, ResourceKey<Enchantment> enchantment) {
        RegistryAccess registryManager = Mob_battle.SERVER.registryAccess();
        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = registryManager.lookupOrThrow(Registries.ENCHANTMENT);
        return itemStack.getEnchantments().getLevel(enchantmentRegistry.getOrThrow(enchantment)) != 0;
    }
    public static boolean hasEnchantment(Player player, ItemStack itemStack, ResourceKey<Enchantment> enchantment) {
        RegistryAccess registryManager = player.registryAccess();
        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = registryManager.lookupOrThrow(Registries.ENCHANTMENT);
        return itemStack.getEnchantments().getLevel(enchantmentRegistry.getOrThrow(enchantment)) != 0;
    }
    public static int getEnchantmentLevel(Level world, ItemStack itemStack, ResourceKey<Enchantment> enchantment) {
        RegistryAccess registryManager = world.registryAccess();
        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = registryManager.lookupOrThrow(Registries.ENCHANTMENT);
        return itemStack.getEnchantments().getLevel(enchantmentRegistry.getOrThrow(enchantment));
    }
    public static int getEnchantmentLevel(Player player, ItemStack itemStack, ResourceKey<Enchantment> enchantment) {
        RegistryAccess registryManager = player.registryAccess();
        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = registryManager.lookupOrThrow(Registries.ENCHANTMENT);
        return itemStack.getEnchantments().getLevel(enchantmentRegistry.getOrThrow(enchantment));
    }
    public static int getEnchantmentLevel(ItemStack itemStack, ResourceKey<Enchantment> enchantment) {
        RegistryAccess registryManager = Mob_battle.SERVER.registryAccess();
        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = registryManager.lookupOrThrow(Registries.ENCHANTMENT);
        return itemStack.getEnchantments().getLevel(enchantmentRegistry.getOrThrow(enchantment));
    }

    public static void addEnchantment(Level world, ItemStack itemStack, ResourceKey<Enchantment> enchantment, int level) {
        RegistryAccess registryManager = world.registryAccess();
        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = registryManager.lookupOrThrow(Registries.ENCHANTMENT);
        itemStack.enchant(new EnchantmentInstance(enchantmentRegistry.getOrThrow(enchantment), level).enchantment(), level);
    }

    public static void addEnchantment(Player player, ItemStack itemStack, ResourceKey<Enchantment> enchantment, int level) {
        RegistryAccess registryManager = player.registryAccess();
        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = registryManager.lookupOrThrow(Registries.ENCHANTMENT);
        itemStack.enchant(new EnchantmentInstance(enchantmentRegistry.getOrThrow(enchantment), level).enchantment(), level);
    }
    public static void addEnchantment(ItemStack itemStack, ResourceKey<Enchantment> enchantment, int level) {
        RegistryAccess registryManager = Mob_battle.SERVER.registryAccess();
        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = registryManager.lookupOrThrow(Registries.ENCHANTMENT);
        itemStack.enchant(new EnchantmentInstance(enchantmentRegistry.getOrThrow(enchantment), level).enchantment(), level);
    }
}
