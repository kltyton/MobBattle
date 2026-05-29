package com.kltyton.mob_battle.mixin.client.render;

import com.kltyton.mob_battle.items.tool.BaseBow;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractClientPlayer.class, priority = 0)
public abstract class AbstractClientPlayerEntityMixin extends Player {
    public AbstractClientPlayerEntityMixin(Level world, GameProfile profile) {
        super(world, profile);
    }

    @Redirect(method = "getFieldOfViewModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    public boolean getFovMultiplier(ItemStack instance, Item item) {
        if (instance.getItem() instanceof BaseBow) return true;
        return instance.is(item);
    }
    @Inject(method = "getFieldOfViewModifier", at = @At("RETURN"), cancellable = true)
    public void getFovMultiplier(boolean firstPerson, float fovEffectScale, CallbackInfoReturnable<Float> cir) {
        if (firstPerson && this.isScoping()) {
            cir.setReturnValue(0.1f);
        }
    }
    @Redirect(method = "getFieldOfViewModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    public double getFovMultiplier(AbstractClientPlayer instance, Holder<Attribute> registryEntry) {
        return Math.min(instance.getAttributeValue(registryEntry), 0.16f);
    }
}
