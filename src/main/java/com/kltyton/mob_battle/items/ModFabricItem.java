package com.kltyton.mob_battle.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public interface ModFabricItem {
    default void onLeftClickStart(PlayerEntity player, ItemStack stack, boolean isServer) {

    }
    default void onLeftClickStop(PlayerEntity player, ItemStack stack, boolean isServer) {

    }
    default void itemEntityHook(ItemStack stack, ItemEntity itemEntity) {

    }
    default void inventoryTick(ItemStack stack, World world, Entity entity, EquipmentSlot slot) {

    }
    default void onDurabilityChange(ItemStack stack, int amount, ServerPlayerEntity entity) {}
}
