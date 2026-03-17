package com.kltyton.mob_battle.mixin.maxstack;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Item.Settings.class)
public class ItemSettingsMixin {
    @ModifyVariable(method = "maxCount", at = @At("HEAD"), argsOnly = true)
    private int getMaxCountPerStack(int original) {
        if (original > 1) return Mob_battle.MAX_STACK_SIZE;
        return original;
    }
}
