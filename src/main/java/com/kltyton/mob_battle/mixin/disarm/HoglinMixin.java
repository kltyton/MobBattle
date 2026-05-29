package com.kltyton.mob_battle.mixin.disarm;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoglinBase.class)
public interface HoglinMixin {
    @Inject(method = "hurtAndThrowTarget", at = @At("HEAD"), cancellable = true)
    private static void cancelMeleeAttack(ServerLevel world, LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (!ModSkillEntityType.canSkill(attacker)) {
            cir.setReturnValue(false);
        }
    }
}
