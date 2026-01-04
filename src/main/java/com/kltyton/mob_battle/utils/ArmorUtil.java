package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.enchantment.ModEnchantments;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ArmorUtil {
    public static boolean hasFullDiamondArmor(LivingEntity entity) {
        ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);

        return head.isOf(ModItems.IRON_GOLD_HELMET) &&
                chest.isOf(ModItems.IRON_GOLD_CHESTPLATE) &&
                legs.isOf(ModItems.IRON_GOLD_LEGGINGS) &&
                feet.isOf(ModItems.IRON_GOLD_BOOTS);
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

        return totalLevel;
    }

}
