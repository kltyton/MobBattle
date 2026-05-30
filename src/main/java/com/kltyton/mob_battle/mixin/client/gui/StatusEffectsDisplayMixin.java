package com.kltyton.mob_battle.mixin.client.gui;

import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EffectsInInventory.class)
public class StatusEffectsDisplayMixin {
    @Inject(method = "getEffectName", at = @At("RETURN"), cancellable = true)
    private void mobBattle$useNumericHighAmplifier(MobEffectInstance statusEffect, CallbackInfoReturnable<Component> cir) {
        int level = statusEffect.getAmplifier() + 1;
        if (level <= 10) {
            return;
        }
        MutableComponent text = statusEffect.getEffect().value().getDisplayName().copy();
        text.append(CommonComponents.SPACE).append(Component.literal(String.valueOf(level)));
        cir.setReturnValue(text);
    }
}
