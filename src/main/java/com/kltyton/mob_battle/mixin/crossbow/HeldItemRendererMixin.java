package com.kltyton.mob_battle.mixin.crossbow;

import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemInHandRenderer.class)
public class HeldItemRendererMixin {
    @Redirect(method = "evaluateWhichHandsToRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z", ordinal = 2))
    private static boolean isOf(ItemStack instance, Object item) {
        return item instanceof Item originalItem && instance.is(originalItem) || instance.is(ModItems.VS_SNIPE);
    }
    @Redirect(method = "evaluateWhichHandsToRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z", ordinal = 3))
    private static boolean isOf2(ItemStack instance, Object item) {
        return item instanceof Item originalItem && instance.is(originalItem) || instance.is(ModItems.VS_SNIPE);
    }
    @Redirect(method = "selectionUsingItemWhileHoldingBowLike", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z", ordinal = 1))
    private static boolean isOf3(ItemStack instance, Object item) {
        return item instanceof Item originalItem && instance.is(originalItem) || instance.is(ModItems.VS_SNIPE);
    }
    @Redirect(method = "isChargedCrossbow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private static boolean isOf4(ItemStack instance, Object item) {
        return item instanceof Item originalItem && instance.is(originalItem) || instance.is(ModItems.VS_SNIPE);
    }
    @Redirect(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private static boolean isOf5(ItemStack instance, Object item) {
        return item instanceof Item originalItem && instance.is(originalItem) || instance.is(ModItems.VS_SNIPE);
    }
}
