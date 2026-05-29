package com.kltyton.mob_battle.utils.keybind;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.kltyton.mob_battle.mixin.accessor.keybind.TimesPressedAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.Nullable;

public class KeybindFixer {
    public static final KeybindFixer INSTANCE = new KeybindFixer();

    private final Multimap<InputConstants.Key, KeyMapping> keyFixMap = ArrayListMultimap.create();

    public void putKey(InputConstants.Key key, KeyMapping keyBinding) {
        keyFixMap.put(key, keyBinding);
    }

    public void clearMap() {
        keyFixMap.clear();
    }

    public void onKeyPressed(InputConstants.Key key, @Nullable KeyMapping finalBinding, @Nullable KeyMapping baseBinding) {
        if (baseBinding == null || finalBinding != baseBinding) return;

        for (KeyMapping theKey : keyFixMap.get(key)) {
            // 核心: 如果这个按键被绑定了，并且不是最终绑定的按键，那么就强制修改它的 timesPressed
            if (theKey == null || theKey == baseBinding) continue;

            // 使用 Accessor 强制修改私有的 timesPressed
            ((TimesPressedAccessor) theKey).setTimesPressed(((TimesPressedAccessor) theKey).getTimesPressed() + 1);
        }
    }

    public void setKeyPressed(InputConstants.Key key, boolean pressed, @Nullable KeyMapping finalBinding, @Nullable KeyMapping baseBinding) {
        if (baseBinding == null || finalBinding != baseBinding) return;

        for (KeyMapping theKey : keyFixMap.get(key)) {
            if (theKey == null || theKey == baseBinding) continue;
            theKey.setDown(pressed);
        }
    }
}