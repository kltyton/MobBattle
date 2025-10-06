package com.kltyton.mob_battle.mixin.client.render;

import com.kltyton.mob_battle.items.tool.BaseBow;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AbstractClientPlayerEntity.class, priority = 0)
public class AbstractClientPlayerEntityMixin {
    @Redirect(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    public boolean getFovMultiplier(ItemStack instance, Item item) {
        if (instance.getItem() instanceof BaseBow) return true;
        return instance.isOf(item);
    }
}
