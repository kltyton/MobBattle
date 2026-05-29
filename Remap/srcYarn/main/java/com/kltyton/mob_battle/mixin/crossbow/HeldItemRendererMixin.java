package com.kltyton.mob_battle.mixin.crossbow;

import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Redirect(method = "getHandRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 2))
    private static boolean isOf(ItemStack instance, Item item) {
        return instance.isOf(item) || instance.isOf(ModItems.VS_SNIPE);
    }
    @Redirect(method = "getHandRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 3))
    private static boolean isOf2(ItemStack instance, Item item) {
        return instance.isOf(item) || instance.isOf(ModItems.VS_SNIPE);
    }
    @Redirect(method = "getUsingItemHandRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 1))
    private static boolean isOf3(ItemStack instance, Item item) {
        return instance.isOf(item) || instance.isOf(ModItems.VS_SNIPE);
    }
    @Redirect(method = "isChargedCrossbow", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean isOf4(ItemStack instance, Item item) {
        return instance.isOf(item) || instance.isOf(ModItems.VS_SNIPE);
    }
    @Redirect(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean isOf5(ItemStack instance, Item item) {
        return instance.isOf(item) || instance.isOf(ModItems.VS_SNIPE);
    }
}
