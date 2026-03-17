package com.kltyton.mob_battle.mixin.maxstack;

import com.kltyton.mob_battle.Mob_battle;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Inventory.class)
public interface InventoryStackMixin {
    /**
    * @ 作者Lonk
    * <p>
    * 这需要将99的常量更改为我们要替换为最大堆栈大小的变量，
    * 从技术上讲，这可能是最好的描述为2.147B，但我不确定这是否会产生明显的距离
    * 这种重写是为了阻止我们要求覆盖和注入以及最终对我们不利的早期回报
    * mod兼容性。
    */

    // 1.2.2-Lonk-此重写允许多个mod注入此而不会引发错误。
    @ModifyReturnValue
            (
                    method = "getMaxCountPerStack()I",
                    at = @At("RETURN")
            )
    default int getMaxCountPerStack(int constant) {
        if (constant != 99) {
            return constant;
        }
        return Mob_battle.MAX_STACK_SIZE; //我们忽略原来的，我们可以做一个检查，以确保它是64，但是，这应该始终是64，这是基本情况。
    }


}
