package com.kltyton.mob_battle.mixin.client.render;

import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void mob_battle$copyIceEffect(LivingEntity entity, LivingEntityRenderState state, float tickDelta, CallbackInfo ci) {
        int amplifier = -1;
        StatusEffectInstance effect = entity.getStatusEffect(ModEffects.ICE_ENTRY);
        if (effect != null) {
            amplifier = effect.getAmplifier();
        }
        ((IModEntityRenderState)state).setIceAmplifier(amplifier);
    }
}
