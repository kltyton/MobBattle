package com.kltyton.mob_battle.mixin.undead;

import net.minecraft.entity.effect.InstantHealthOrDamageStatusEffect;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InstantHealthOrDamageStatusEffect.class)
public class InstantHealthOrDamageStatusEffectMixin {
/*    @Redirect(method = "applyInstantEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean cancelInstantHealthOrDamage(LivingEntity instance, ServerWorld world, DamageSource source, float amount) {
        if (instance.getType().isIn(EntityTypeTags.UNDEAD)) {
            instance.heal(amount);// 撤销对亡灵的伤害
            return false;
        } else {
            return instance.damage(world, source, amount);
        }
    }
    @Redirect(method = "applyUpdateEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean cancelUpdateEffect(LivingEntity instance, ServerWorld world, DamageSource source, float amount) {
        if (instance.getType().isIn(EntityTypeTags.UNDEAD)) {
            instance.heal(amount);// 撤销对亡灵的伤害
            return false;
        } else {
            return instance.damage(world, source, amount);
        }
    }*/
}
