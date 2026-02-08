package com.kltyton.mob_battle.mixin.disarm;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Hoglin.class)
public interface HoglinMixin {
    @Inject(method = "tryAttack", at = @At("HEAD"), cancellable = true)
    private static void cancelMeleeAttack(ServerWorld world, LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (!ModSkillEntityType.canSkill(attacker)) {
            cir.setReturnValue(false);
        }
    }
}
