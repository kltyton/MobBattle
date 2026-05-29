package com.kltyton.mob_battle.mixin.effect;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.effect.harmful.StunEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityStatusEffectMixin {
    @Inject(method = "onEffectsRemoved", at = @At("TAIL"))
    private void mobBattle$restoreStunAiState(Collection<MobEffectInstance> effects, CallbackInfo ci) {
        for (MobEffectInstance effect : effects) {
            if (effect.getEffect().equals(ModEffects.STUN_ENTRY)) {
                StunEffect.restoreAiState((LivingEntity) (Object) this);
                return;
            }
        }
    }
}
