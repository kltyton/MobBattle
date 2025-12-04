package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class IronGoldArmorUtil {
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
}
