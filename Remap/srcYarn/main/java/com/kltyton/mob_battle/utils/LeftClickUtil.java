package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.items.ModFabricItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class LeftClickUtil {
    public static void leftClick(PlayerEntity player, boolean isPressed, boolean isServer) {
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() instanceof ModFabricItem modFabricItem) {
            if (isPressed) {
                modFabricItem.onLeftClickStart(player, stack, isServer);
            } else {
                modFabricItem.onLeftClickStop(player, stack, isServer);
            }
        }
    }
}
