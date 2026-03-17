package com.kltyton.mob_battle.mixin.maxstack;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    /**
    * {@code @作者Lonkachu}
    * 此代码是对渲染问题的创可贴，这些问题来自将堆栈大小扩展到999以上，因为文本将开始蠕变到块的其他部分，
    * 我想在某些时候用自动调整文本替换这个，也许下次重写。
    * ModifyVariable是本节的最佳选择，因为此方法恰好包含一个仅在块文本未覆盖时才使用的字符串，非常简便
     */

    @ModifyVariable(method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private String modifyString(String value, TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String countOverride) {
        int count = stack.getCount();
        if(count > 999999999) {
            count /= 1000000000;
            return count + "B";
        }
        else if (count > 999999) {
            count /= 1000000;
            return count + "M";
        }
        else if (count > 999) {
            count /= 1000;
            return count + "K";
        }
        else if (count > 1) {
            return String.valueOf(count);
        }
        return "";
    }
}
