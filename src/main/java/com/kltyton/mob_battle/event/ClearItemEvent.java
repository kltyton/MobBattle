package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.Mob_battle;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class ClearItemEvent {

    // 全局缓存：所有本模组物品的 rawId（最快比对方式）
    private static final IntOpenHashSet BANNED_ITEM_RAW_IDS = new IntOpenHashSet();

    public static void init() {
        collectBannedItems();
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> clearPlayerInventory(handler.getPlayer()));
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % 10 == 0) {
                server.getPlayerManager().getPlayerList().forEach(ClearItemEvent::clearPlayerInventory);
            }
        });
    }
    public static void collectBannedItems() {
        BANNED_ITEM_RAW_IDS.clear();
        Registries.ITEM.forEach(item -> {
            Identifier id = Registries.ITEM.getId(item);
            if (Mob_battle.MOD_ID.equals(id.getNamespace())) {
                BANNED_ITEM_RAW_IDS.add(Item.getRawId(item));
            }
        });
    }

    private static boolean isBannedItem(ItemStack stack) {
        return !stack.isEmpty() && isBannedRawId(Item.getRawId(stack.getItem()));
    }

    private static boolean isBannedRawId(int rawId) {
        return BANNED_ITEM_RAW_IDS.contains(rawId);
    }

    public static void clearPlayerInventory(ServerPlayerEntity player) {
        //if (player.interactionManager.isCreative()) return;
        if (player.hasPermissionLevel(2) ||
                (player.getCommandTags().contains("swmg") && player.getCommandTags().contains("shen"))) {
            return;
        }
        var inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (isBannedItem(stack)) {
                inv.setStack(i, ItemStack.EMPTY);
            }
        }
        ItemStack cursor = player.currentScreenHandler.getCursorStack();
        if (isBannedItem(cursor)) {
            player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
        }
    }
}