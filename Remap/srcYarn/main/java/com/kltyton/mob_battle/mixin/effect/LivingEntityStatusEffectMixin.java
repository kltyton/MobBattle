package com.kltyton.mob_battle.mixin.effect;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.effect.harmful.StunEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(LivingEntity.class)
public class LivingEntityStatusEffectMixin {
    @Inject(method = "onStatusEffectsRemoved", at = @At("TAIL"))
    private void mobBattle$restoreStunAiState(Collection<StatusEffectInstance> effects, CallbackInfo ci) {
        for (StatusEffectInstance effect : effects) {
            if (effect.getEffectType().equals(ModEffects.STUN_ENTRY)) {
                StunEffect.restoreAiState((LivingEntity) (Object) this);
                return;
            }
        }
    }
}
