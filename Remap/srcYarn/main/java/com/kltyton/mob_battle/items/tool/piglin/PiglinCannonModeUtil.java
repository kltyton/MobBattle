package com.kltyton.mob_battle.items.tool.piglin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public final class PiglinCannonModeUtil {
    private PiglinCannonModeUtil() {}

    private static final String MODE_KEY = "PiglinCannonMode";

    public enum Mode {
        FAST_FIRE,
        HEAVY_BLAST
    }

    public static Mode getMode(ItemStack stack) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        String name = nbt.getString(MODE_KEY, Mode.FAST_FIRE.name());
        if ("HEAVY_BLAST".equals(name)) {
            return Mode.HEAVY_BLAST;
        }
        return Mode.FAST_FIRE;
    }

    public static void setMode(ItemStack stack, Mode mode) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        nbt.putString(MODE_KEY, mode.name());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public static Mode toggleMode(ItemStack stack) {
        Mode current = getMode(stack);
        Mode next = current == Mode.FAST_FIRE ? Mode.HEAVY_BLAST : Mode.FAST_FIRE;
        setMode(stack, next);
        return next;
    }
}