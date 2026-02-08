package com.kltyton.mob_battle.mixin.disarm;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 900)
public abstract class LivingEntityDisarmMixin {
    @Inject(method = "setCurrentHand", at = @At("HEAD"), cancellable = true)
    private void preventItemUse(Hand hand, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!ModSkillEntityType.canSkill(entity)) {
            ci.cancel();
        }
    }
}
