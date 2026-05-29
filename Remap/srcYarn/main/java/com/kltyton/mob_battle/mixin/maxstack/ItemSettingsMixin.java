package com.kltyton.mob_battle.mixin.maxstack;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.Settings.class)
public class ItemSettingsMixin {
    //将所有堆栈大小限制为1024000000
/*    @ModifyVariable(method = "maxCount", at = @At("HEAD"), argsOnly = true)
    private int getMaxCountPerStack(int original) {
        if (original > 1) return Mob_battle.MAX_STACK_SIZE;
        return original;
    }*/
}
