package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.enchantment.ModEnchantments;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public class ArmorUtil {
    public static boolean hasFullArmor(LivingEntity entity, ArmorMaterial material) {
        RegistryKey<EquipmentAsset> assetId = material.assetId();

        return hasArmorWithAsset(entity, EquipmentSlot.HEAD, assetId)
                && hasArmorWithAsset(entity, EquipmentSlot.CHEST, assetId)
                && hasArmorWithAsset(entity, EquipmentSlot.LEGS, assetId)
                && hasArmorWithAsset(entity, EquipmentSlot.FEET, assetId);
    }

    private static boolean hasArmorWithAsset(
            LivingEntity entity,
            EquipmentSlot slot,
            RegistryKey<EquipmentAsset> assetId
    ) {
        ItemStack stack = entity.getEquippedStack(slot);
        if (stack.isEmpty()) {
            return false;
        }

        EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);
        if (equippable == null) {
            return false;
        }

        if (equippable.slot() != slot) {
            return false;
        }

        return equippable.assetId().isPresent()
                && equippable.assetId().get().equals(assetId);
    }

    public static int getMagicProtectionLevel(LivingEntity entity) {
        World world = entity.getWorld();
        EquipmentSlot[] slots = {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        };

        int totalLevel = 0;

        for (EquipmentSlot slot : slots) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                totalLevel += EnchantmentUtil.getEnchantmentLevel(
                        world,
                        stack,
                        ModEnchantments.MAGIC_PROTECTION
                );
            }
        }

        if (totalLevel != 0) {
            StatusEffectInstance effect = entity.getStatusEffect(ModEffects.VOID_ARMOR_PIERCING_ENTRY);
            if (effect != null) {
                int level = effect.getAmplifier() + 1;
                return Math.max(0, totalLevel - level);
            }
        }

        return totalLevel;
    }
}