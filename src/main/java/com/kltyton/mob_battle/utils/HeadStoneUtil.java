package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.items.ModItems;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HeadStoneUtil {
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
