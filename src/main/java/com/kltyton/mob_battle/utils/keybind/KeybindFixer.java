package com.kltyton.mob_battle.utils.keybind;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.kltyton.mob_battle.mixin.accessor.keybind.TimesPressedAccessor;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.Nullable;

public class KeybindFixer {
    // 这里的实例模拟了 Kotlin 的 INSTANCE
    public static final KeybindFixer INSTANCE = new KeybindFixer();

    private final Multimap<InputUtil.Key, KeyBinding> keyFixMap = ArrayListMultimap.create();

    public void putKey(InputUtil.Key key, KeyBinding keyBinding) {
        keyFixMap.put(key, keyBinding);
    }

    public void clearMap() {
        keyFixMap.clear();
    }

    public void onKeyPressed(InputUtil.Key key, @Nullable KeyBinding finalBinding, @Nullable KeyBinding baseBinding) {
        if (baseBinding == null || finalBinding != baseBinding) return;

        for (KeyBinding theKey : keyFixMap.get(key)) {
            // 如果这个按键不是原版 Map 里那个“幸运儿”，我们就手动给它补一张票
            if (theKey == null || theKey == baseBinding) continue;

            // 使用 Accessor 强制修改私有的 timesPressed
            ((TimesPressedAccessor) theKey).setTimesPressed(((TimesPressedAccessor) theKey).getTimesPressed() + 1);
        }
    }

    public void setKeyPressed(InputUtil.Key key, boolean pressed, @Nullable KeyBinding finalBinding, @Nullable KeyBinding baseBinding) {
        if (baseBinding == null || finalBinding != baseBinding) return;

        for (KeyBinding theKey : keyFixMap.get(key)) {
            if (theKey == null || theKey == baseBinding) continue;
            theKey.setPressed(pressed);
        }
    }
}