package com.kltyton.mob_battle.mixin.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LlamaSpit.class)
public class LlamaSpitMixin {
    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void mobBattle$clearInvulnerabilityBeforeHit(EntityHitResult hitResult, CallbackInfo ci) {
        clearInvulnerability(hitResult.getEntity());
    }

    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void mobBattle$clearInvulnerabilityAfterHit(EntityHitResult hitResult, CallbackInfo ci) {
        clearInvulnerability(hitResult.getEntity());
    }

    private static void clearInvulnerability(Entity entity) {
        if (entity instanceof LivingEntity living) {
            living.invulnerableTime = 0;
        }
    }
}
