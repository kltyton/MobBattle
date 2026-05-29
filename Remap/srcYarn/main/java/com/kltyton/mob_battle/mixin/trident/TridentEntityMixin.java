package com.kltyton.mob_battle.mixin.trident;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin {
    @ModifyVariable(method = "onEntityHit", at = @At("STORE"), ordinal = 0)
    private float useOwnerMeleeTridentDamage(float damage) {
        Entity owner = ((ProjectileEntity) (Object) this).getOwner();
        if (owner instanceof LivingEntity living) {
            return Math.max(damage, (float) living.getAttributeValue(EntityAttributes.ATTACK_DAMAGE));
        }
        return damage + 1.0F;
    }
}
