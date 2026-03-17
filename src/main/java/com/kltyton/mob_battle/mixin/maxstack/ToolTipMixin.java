package com.kltyton.mob_battle.mixin.maxstack;

import com.kltyton.mob_battle.Mob_battle;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
/*
@ ItemStackFixin
作者: Lonk
这个mixin类的存在是为了解开如果块堆栈高于99发生的崩溃，我不太清楚为什么mojang添加了这个
tbh，它不会对人们的
*/
@Mixin(ItemStack.class)
public abstract class ToolTipMixin {
    /**
    * 在项目工具提示中添加完整计数。
    * @ stacc的作者Devin-Kerman，更新为1.21
    */
    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void addOverflowTooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        if (this.getCount() > 999) {
            List<Text> texts = cir.getReturnValue();
            texts.add(1, Text.literal("物品数量：" + this.getCount()).formatted(Formatting.GRAY));
        }
    }
    //1.2.2-这是一个更清洁，更不容易崩溃。这样做是为了解决kubeJS的问题，可能值得为他们做一个公关，因为这应该达到相同的效果，并防止其他mods崩溃。
    @ModifyExpressionValue
            (
                    method = "method_57371", //这种方法是一个Lambda，他们不是funda。
                    at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/Codecs;rangedInt(II)Lcom/mojang/serialization/Codec;")
            )
    private static Codec<Integer> replaceCodec(Codec<Integer> original) {
        return Codecs.rangedInt(0, Mob_battle.MAX_STACK_SIZE);
    }

    @Shadow
    public abstract int getCount();
}
