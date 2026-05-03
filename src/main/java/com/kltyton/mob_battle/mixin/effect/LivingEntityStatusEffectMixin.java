package com.kltyton.mob_battle.mixin.effect;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.effect.harmful.StunEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityStatusEffectMixin {
    @Inject(method = "removeStatusEffectInternal", at = @At("RETURN"))
    private void mobBattle$restoreStunAiState(RegistryEntry<StatusEffect> effect, CallbackInfoReturnable<StatusEffectInstance> cir) {
        if (cir.getReturnValue() != null && effect.equals(ModEffects.STUN_ENTRY)) {
            StunEffect.restoreAiState((LivingEntity) (Object) this);
        }
    }
}
