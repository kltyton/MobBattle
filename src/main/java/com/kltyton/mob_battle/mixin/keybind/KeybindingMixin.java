package com.kltyton.mob_battle.mixin.keybind;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = KeyMapping.class, priority = 10000)
public abstract class KeybindingMixin {
}
