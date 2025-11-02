package com.kltyton.mob_battle.mixin.client.render;

import com.kltyton.mob_battle.items.tool.BaseBow;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractClientPlayerEntity.class, priority = 0)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {
    public AbstractClientPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Redirect(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    public boolean getFovMultiplier(ItemStack instance, Item item) {
        if (instance.getItem() instanceof BaseBow) return true;
        return instance.isOf(item);
    }
    @Inject(method = "getFovMultiplier", at = @At("RETURN"), cancellable = true)
    public void getFovMultiplier(boolean firstPerson, float fovEffectScale, CallbackInfoReturnable<Float> cir) {
        if (firstPerson && this.isUsingSpyglass()) {
            cir.setReturnValue(0.1f);
        }
    }
}
