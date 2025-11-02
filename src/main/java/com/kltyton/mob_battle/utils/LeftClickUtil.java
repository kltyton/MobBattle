package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.items.FabricItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class LeftClickUtil {
    public static void leftClick(PlayerEntity player, boolean isPressed, boolean isServer) {
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() instanceof FabricItem fabricItem) {
            if (isPressed) {
                fabricItem.onLeftClickStart(player, stack, isServer);
            } else {
                fabricItem.onLeftClickStop(player, stack, isServer);
            }
        }
    }
}
