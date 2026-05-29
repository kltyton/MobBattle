package com.kltyton.mob_battle.config.whitelist;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class ClientItemFilter {

    public static boolean shouldHide(ItemStack stack) {
        if (ClientPermissionState.isWhitelisted()) return false;
        if (stack == null || stack.isEmpty()) return false;

        Identifier id = Registries.ITEM.getId(stack.getItem());
        return Mob_battle.MOD_ID.equals(id.getNamespace());
    }
    public static boolean shouldHideSuggestion(String text) {
        if (text == null || text.isEmpty()) return false;
        return text.contains(Mob_battle.MOD_ID + ":");
    }
}
