package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.items.ModFabricItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class LeftClickUtil {
    public static void leftClick(Player player, boolean isPressed, boolean isServer) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof ModFabricItem modFabricItem) {
            if (isPressed) {
                modFabricItem.onLeftClickStart(player, stack, isServer);
            } else {
                modFabricItem.onLeftClickStop(player, stack, isServer);
            }
        }
    }
}
