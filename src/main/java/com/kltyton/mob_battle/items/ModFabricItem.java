package com.kltyton.mob_battle.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ModFabricItem {
    default void onLeftClickStart(Player player, ItemStack stack, boolean isServer) {

    }
    default void onLeftClickStop(Player player, ItemStack stack, boolean isServer) {

    }
    default void itemEntityHook(ItemStack stack, ItemEntity itemEntity) {

    }
    default void inventoryTick(ItemStack stack, Level world, Entity entity, EquipmentSlot slot) {

    }
    default void onDurabilityChange(ItemStack stack, int amount, ServerPlayer entity) {}

    default void onSuccessfulCriticalHit(Player player, Entity target, ItemStack stack) {}
    default void onSuccessfulSweepHit(Player player, Entity target, ItemStack stack) {}
}
