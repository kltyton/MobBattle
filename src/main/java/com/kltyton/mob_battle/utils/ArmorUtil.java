package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.enchantment.ModEnchantments;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.ModMaterial;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.world.World;

public class ArmorUtil {
    public static boolean hasFullArmor(LivingEntity entity, ArmorMaterial material) {
        ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);
        if (material == ModMaterial.IRON_GOLD_INSTANCE) return
                head.isOf(ModItems.IRON_GOLD_HELMET) &&
                chest.isOf(ModItems.IRON_GOLD_CHESTPLATE) &&
                legs.isOf(ModItems.IRON_GOLD_LEGGINGS) &&
                feet.isOf(ModItems.IRON_GOLD_BOOTS);

        if (material == ModMaterial.ECREDCULTIST_INSTANCE) return
                head.isOf(ModItems.ECREDCULTIST_HELMET) &&
                chest.isOf(ModItems.ECREDCULTIST_CHESTPLATE) &&
                legs.isOf(ModItems.ECREDCULTIST_LEGGINGS) &&
                feet.isOf(ModItems.ECREDCULTIST_BOOTS);

        if (material == ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE) return
                head.isOf(ModItems.EMERALD_DIAMOND_HELMET) &&
                chest.isOf(ModItems.EMERALD_DIAMOND_CHESTPLATE) &&
                legs.isOf(ModItems.EMERALD_DIAMOND_LEGGINGS) &&
                feet.isOf(ModItems.EMERALD_DIAMOND_BOOTS);

        return false;
    }
    public static int getMagicProtectionLevel(LivingEntity entity) {
        World world = entity.getWorld();
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        int totalLevel = 0;

        for (EquipmentSlot slot : slots) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (stack != null) {
                totalLevel += EnchantmentUtil.getEnchantmentLevel(world, stack, ModEnchantments.MAGIC_PROTECTION);
            }
        }
        if (totalLevel != 0) {
            StatusEffectInstance effect = entity.getStatusEffect(ModEffects.VOID_ARMOR_PIERCING_ENTRY);
            if (effect != null) {
                int level = effect.getAmplifier() + 1;
                int newProtection = totalLevel - level;
                newProtection = Math.max(0, newProtection);
                return newProtection;
            }
        }
        return totalLevel;
    }

}
