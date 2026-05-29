package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.enchantment.ModEnchantments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;

public class ArmorUtil {
    public static boolean hasFullArmor(LivingEntity entity, ArmorMaterial material) {
        ResourceKey<EquipmentAsset> assetId = material.assetId();

        return hasArmorWithAsset(entity, EquipmentSlot.HEAD, assetId)
                && hasArmorWithAsset(entity, EquipmentSlot.CHEST, assetId)
                && hasArmorWithAsset(entity, EquipmentSlot.LEGS, assetId)
                && hasArmorWithAsset(entity, EquipmentSlot.FEET, assetId);
    }

    private static boolean hasArmorWithAsset(
            LivingEntity entity,
            EquipmentSlot slot,
            ResourceKey<EquipmentAsset> assetId
    ) {
        ItemStack stack = entity.getItemBySlot(slot);
        if (stack.isEmpty()) {
            return false;
        }

        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
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
        Level world = entity.level();
        EquipmentSlot[] slots = {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        };

        int totalLevel = 0;

        for (EquipmentSlot slot : slots) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                totalLevel += EnchantmentUtil.getEnchantmentLevel(
                        world,
                        stack,
                        ModEnchantments.MAGIC_PROTECTION
                );
            }
        }

        if (totalLevel != 0) {
            MobEffectInstance effect = entity.getEffect(ModEffects.VOID_ARMOR_PIERCING_ENTRY);
            if (effect != null) {
                int level = effect.getAmplifier() + 1;
                return Math.max(0, totalLevel - level);
            }
        }

        return totalLevel;
    }
}