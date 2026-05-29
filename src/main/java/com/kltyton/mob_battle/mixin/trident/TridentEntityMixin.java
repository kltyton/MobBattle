package com.kltyton.mob_battle.mixin.trident;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ThrownTrident.class)
public abstract class TridentEntityMixin {
    @ModifyVariable(method = "onHitEntity", at = @At("STORE"), ordinal = 0)
    private float useOwnerMeleeTridentDamage(float damage) {
        Entity owner = ((Projectile) (Object) this).getOwner();
        if (owner instanceof LivingEntity living) {
            return Math.max(damage, (float) living.getAttributeValue(Attributes.ATTACK_DAMAGE));
        }
        return damage + 1.0F;
    }
}
