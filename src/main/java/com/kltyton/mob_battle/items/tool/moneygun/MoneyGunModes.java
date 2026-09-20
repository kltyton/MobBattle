package com.kltyton.mob_battle.items.tool.moneygun;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * 撒币枪模式在物品自定义数据中的稳定存档协议。
 */
public final class MoneyGunModes {
    private static final String MODE_KEY = "MoneyGunMode";

    private MoneyGunModes() {
    }

    public enum Mode {
        ILLAGER_SEMI("message.mob_battle.money_gun_mode_illager"),
        EMERALD_AUTO("message.mob_battle.money_gun_mode_auto"),
        EMERALD_SHOTGUN("message.mob_battle.money_gun_mode_shotgun");

        private final String translationKey;

        Mode(String translationKey) {
            this.translationKey = translationKey;
        }

        public String translationKey() {
            return translationKey;
        }
    }

    public static Mode getMode(ItemStack stack) {
        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        String name = data.getStringOr(MODE_KEY, Mode.ILLAGER_SEMI.name());
        try {
            return Mode.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return Mode.ILLAGER_SEMI;
        }
    }

    public static void setMode(ItemStack stack, Mode mode) {
        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        data.putString(MODE_KEY, mode.name());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
    }

    public static Mode next(ItemStack stack) {
        Mode next = switch (getMode(stack)) {
            case ILLAGER_SEMI -> Mode.EMERALD_AUTO;
            case EMERALD_AUTO -> Mode.EMERALD_SHOTGUN;
            case EMERALD_SHOTGUN -> Mode.ILLAGER_SEMI;
        };
        setMode(stack, next);
        return next;
    }
}
