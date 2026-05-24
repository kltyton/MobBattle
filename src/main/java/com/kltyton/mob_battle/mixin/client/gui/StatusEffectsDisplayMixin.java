package com.kltyton.mob_battle.mixin.client.gui;

import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectsDisplay.class)
public class StatusEffectsDisplayMixin {
    @Inject(method = "getStatusEffectDescription", at = @At("RETURN"), cancellable = true)
    private void mobBattle$useNumericHighAmplifier(StatusEffectInstance statusEffect, CallbackInfoReturnable<Text> cir) {
        int level = statusEffect.getAmplifier() + 1;
        if (level <= 10) {
            return;
        }
        MutableText text = statusEffect.getEffectType().value().getName().copy();
        text.append(ScreenTexts.SPACE).append(Text.literal(String.valueOf(level)));
        cir.setReturnValue(text);
    }
}
