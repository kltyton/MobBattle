package com.kltyton.mob_battle.mixin.backpack;

import net.minecraft.component.type.ContainerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ContainerComponent.class)
public class ContainerComponentMixin {
    // 1. 修改构造函数中的 256 限制检查
    @ModifyConstant(
            method = "<init>(Lnet/minecraft/util/collection/DefaultedList;)V",
            constant = @Constant(intValue = 256)
    )
    private static int increaseConstructorLimit(int original) {
        return 3000; // 设置一个大于你 2700 的值
    }

    // 2. 修改静态常量字段中的 Codec 限制
    @ModifyConstant(
            method = "<clinit>",
            constant = @Constant(intValue = 256)
    )
    private static int increaseStaticLimit(int original) {
        return 3000;
    }
}
