package com.kltyton.mob_battle.mixin.maxstack;

import com.kltyton.mob_battle.Mob_battle;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import net.minecraft.component.DataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataComponentTypes.class)
public class DataComponentTypesMixin {
    //修改默认堆栈大小
/*    @ModifyExpressionValue
            (
                    method = "<clinit>",
                    at = @At(value = "CONSTANT", args = "intValue=64")
            )
    private static int getMaxCountPerStack(int original) {
        return Mob_battle.MAX_STACK_SIZE;
    }*/
    @ModifyExpressionValue
            (
                    method = "method_58570",
                    at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/Codecs;rangedInt(II)Lcom/mojang/serialization/Codec;")
            )
    private static Codec<Integer> replaceCodec(Codec<Integer> original)
    {
        return Codec.intRange(0, Mob_battle.MAX_STACK_SIZE);
    }
}

