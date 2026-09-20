package com.kltyton.mob_battle.items.heartstone;

import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HeartStoneInventory {
    public static boolean keepInventory(Player player) {
        int count = countHeartStones(player);
        if (count >= 2) {
            // 消耗两个心石
            return true;
        } else {
            return false;
        }
    }

    public static int countHeartStones(Player player) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.HEART_STONE)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static void consumeHeartStones(Player player, int amount) {
        for (int i = 0; i < player.getInventory().getContainerSize() && amount > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.HEART_STONE)) {
                int remove = Math.min(amount, stack.getCount());
                stack.shrink(remove);
                amount -= remove;
            }
        }
    }
}
