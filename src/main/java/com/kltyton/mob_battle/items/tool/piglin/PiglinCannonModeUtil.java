package com.kltyton.mob_battle.items.tool.piglin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class PiglinCannonModeUtil {
    private PiglinCannonModeUtil() {}

    private static final String MODE_KEY = "PiglinCannonMode";

    public enum Mode {
        FAST_FIRE,
        HEAVY_BLAST
    }

    public static Mode getMode(ItemStack stack) {
        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        String name = nbt.getStringOr(MODE_KEY, Mode.FAST_FIRE.name());
        if ("HEAVY_BLAST".equals(name)) {
            return Mode.HEAVY_BLAST;
        }
        return Mode.FAST_FIRE;
    }

    public static void setMode(ItemStack stack, Mode mode) {
        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        nbt.putString(MODE_KEY, mode.name());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }

    public static Mode toggleMode(ItemStack stack) {
        Mode current = getMode(stack);
        Mode next = current == Mode.FAST_FIRE ? Mode.HEAVY_BLAST : Mode.FAST_FIRE;
        setMode(stack, next);
        return next;
    }
}