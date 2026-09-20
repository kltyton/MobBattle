package com.kltyton.mob_battle.items.tool.piglin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * 猪灵火炮模式在物品组件中的读写协议。
 *
 * <p>组件键和枚举名称属于存档兼容数据，不得随意修改。</p>
 */
public final class PiglinCannonModes {
    private PiglinCannonModes() {
    }

    private static final String MODE_KEY = "PiglinCannonMode";

    /** 火炮可选射击模式。 */
    public enum Mode {
        FAST_FIRE,
        HEAVY_BLAST
    }

    /** 读取模式；缺失或未知值安全回退到快速射击。 */
    public static Mode getMode(ItemStack stack) {
        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        String name = nbt.getStringOr(MODE_KEY, Mode.FAST_FIRE.name());
        if ("HEAVY_BLAST".equals(name)) {
            return Mode.HEAVY_BLAST;
        }
        return Mode.FAST_FIRE;
    }

    /** 将模式写回物品自定义数据组件。 */
    public static void setMode(ItemStack stack, Mode mode) {
        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        nbt.putString(MODE_KEY, mode.name());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }

    /** 在两种模式间切换，并返回切换后的模式。 */
    public static Mode toggleMode(ItemStack stack) {
        Mode current = getMode(stack);
        Mode next = current == Mode.FAST_FIRE ? Mode.HEAVY_BLAST : Mode.FAST_FIRE;
        setMode(stack, next);
        return next;
    }
}
