package com.kltyton.mob_battle.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface FabricItem {
    void onLeftClickStart(PlayerEntity player, ItemStack stack, boolean isServer);
    void onLeftClickStop(PlayerEntity player, ItemStack stack, boolean isServer);
}
