package com.kltyton.mob_battle.mixin.entity.zombie;

import com.kltyton.mob_battle.entity.ModEntities;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 允许 mod 弓箭僵尸接受正向生命恢复效果；该例外只针对该实体类型，不改变其他亡灵的药水规则。
 */
@Mixin(LivingEntity.class)
public abstract class BowZombieRegenerationMixin {
    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    private void mob_battle$allowRegeneration(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getType() == ModEntities.BOW_ZOMBIE_MOD && effect.is(MobEffects.REGENERATION)) {
            cir.setReturnValue(true);
        }
    }
}
