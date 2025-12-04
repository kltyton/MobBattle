package com.kltyton.mob_battle.mixin.irongold;

import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"))
    public void updateResult(ItemStack instance, int damage) {
        if (instance.isOf(ModItems.IRON_GOLD_SWORD)) {
            instance.setDamage(0);
        } else {
            instance.setDamage(damage);
        }
    }
}
