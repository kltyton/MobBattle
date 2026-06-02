package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ClearItemEvent {
    // TODO: 物品黑名单缓存,暂时没用
    // 全局缓存：所有本模组物品的 rawId（最快比对方式）
/*    private static final IntOpenHashSet BANNED_ITEM_RAW_IDS = new IntOpenHashSet();

    public static void init() {
        collectBannedItems();
    }
    public static void collectBannedItems() {
        BANNED_ITEM_RAW_IDS.clear();
        BuiltInRegistries.ITEM.forEach(item -> {
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            if (Mob_battle.MOD_ID.equals(id.getNamespace())) {
                BANNED_ITEM_RAW_IDS.add(Item.getId(item));
            }
        });
    }
    public static boolean isModItem(ItemStack stack) {
        return isBannedItem(stack);
    }
    private static boolean isBannedItem(ItemStack stack) {
        return !stack.isEmpty() && isBannedRawId(Item.getId(stack.getItem()));
    }

    private static boolean isBannedRawId(int rawId) {
        return BANNED_ITEM_RAW_IDS.contains(rawId);
    }*/
}
