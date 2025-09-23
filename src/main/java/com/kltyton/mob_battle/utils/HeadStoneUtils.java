package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HeadStoneUtils {
    private static final Map<UUID, Boolean> CACHE = new ConcurrentHashMap<>();

    /* 查询 */
    public static boolean shouldKeep(UUID id) {
        return CACHE.getOrDefault(id, false);
    }

    /* 写入 */
    public static void setKeep(UUID id, boolean keep) {
        if (keep) CACHE.put(id, Boolean.TRUE);
        else CACHE.remove(id);
    }
    public static boolean keepInventory(PlayerEntity player) {
        int count = countHeartStones(player);
        if (count >= 2) {
            // 消耗两个心石
            return true;
        } else {
            return false;
        }
    }

    public static int countHeartStones(PlayerEntity player) {
        int count = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(ModItems.HEART_STONE)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static void consumeHeartStones(PlayerEntity player, int amount) {
        for (int i = 0; i < player.getInventory().size() && amount > 0; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(ModItems.HEART_STONE)) {
                int remove = Math.min(amount, stack.getCount());
                stack.decrement(remove);
                amount -= remove;
            }
        }
    }
}
