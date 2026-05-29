package com.kltyton.mob_battle.mixin.accessor.keybind;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface TimesPressedAccessor {
    @Accessor("timesPressed")
    int getTimesPressed();

    @Accessor("timesPressed")
    void setTimesPressed(int value);
}
