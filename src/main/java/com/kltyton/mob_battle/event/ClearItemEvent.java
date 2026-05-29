package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.whitelist.MobBattlePermissions;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ClearItemEvent {

    // 全局缓存：所有本模组物品的 rawId（最快比对方式）
    private static final IntOpenHashSet BANNED_ITEM_RAW_IDS = new IntOpenHashSet();

    public static void init() {
        collectBannedItems();
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> clearPlayerInventory(handler.getPlayer()));
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 10 == 0) {
                server.getPlayerList().getPlayers().forEach(ClearItemEvent::clearPlayerInventory);
            }
        });
    }
    public static void collectBannedItems() {
        BANNED_ITEM_RAW_IDS.clear();
        BuiltInRegistries.ITEM.forEach(item -> {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
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
    }

    public static void clearPlayerInventory(ServerPlayer player) {
        //if (player.interactionManager.isCreative()) return;
        if (player.hasPermissions(2) || MobBattlePermissions.canUseProtectedContent(player) || player.getTags().contains("swmg")) {
            return;
        }
        var inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (isBannedItem(stack)) {
                inv.setItem(i, ItemStack.EMPTY);
            }
        }
        ItemStack cursor = player.containerMenu.getCarried();
        if (isBannedItem(cursor)) {
            player.containerMenu.setCarried(ItemStack.EMPTY);
        }
    }
}