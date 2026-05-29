package com.kltyton.mob_battle.mixin.accessor.keybind;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface TimesPressedAccessor {
    @Accessor("clickCount")
    int getTimesPressed();

    @Accessor("clickCount")
    void setTimesPressed(int value);
}
