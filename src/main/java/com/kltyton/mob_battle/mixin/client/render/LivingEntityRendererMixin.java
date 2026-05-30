package com.kltyton.mob_battle.mixin.client.render;

import com.kltyton.mob_battle.accessor.IEffectMarker;
import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At("TAIL")
    )
    private void mobBattle$copyModRenderState(LivingEntity entity, LivingEntityRenderState state, float tickDelta, CallbackInfo ci) {
        int iceAmplifier = -1;
        MobEffectInstance iceEffect = entity.getEffect(ModEffects.ICE_ENTRY);
        if (iceEffect != null) {
            iceAmplifier = iceEffect.getAmplifier();
        }

        IEffectMarker marker = (IEffectMarker) entity;
        IModEntityRenderState modState = (IModEntityRenderState) state;

        modState.setIceAmplifier(iceAmplifier);
        modState.setCompressedArmorMarkerType(marker.mobBattle$getCompressedArmorMarkerType());
        modState.setPigSpiritMarkAmplifier(marker.mobBattle$getPigSpiritMarkAmplifier());
    }
}