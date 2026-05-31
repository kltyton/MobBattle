package com.kltyton.mob_battle.mixin.trident;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTrident.class)
public abstract class TridentEntityMixin {
    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
    private void mobBattle$skipFriendlyTridentHit(EntityHitResult hitResult, CallbackInfo ci) {
        Entity owner = ((Projectile) (Object) this).getOwner();
        Entity target = hitResult.getEntity();
        if (owner != null && target instanceof LivingEntity livingTarget
                && (target.isAlliedTo(owner) || owner.isAlliedTo(target) || EntityUtil.shouldBlockOwnedSummonDamage(owner, livingTarget))) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "onHitEntity", at = @At("STORE"), ordinal = 0)
    private float useOwnerMeleeTridentDamage(float damage) {
        Entity owner = ((Projectile) (Object) this).getOwner();
        if (owner instanceof LivingEntity living) {
            return Math.max(damage, (float) living.getAttributeValue(Attributes.ATTACK_DAMAGE));
        }
        return damage + 1.0F;
    }
}
