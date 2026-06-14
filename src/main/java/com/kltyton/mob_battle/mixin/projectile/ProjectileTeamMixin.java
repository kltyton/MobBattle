package com.kltyton.mob_battle.mixin.projectile;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Projectile.class)
public abstract class ProjectileTeamMixin {
    @Inject(method = "canHitEntity(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void mobBattle$skipOwnedSummonAllies(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Projectile projectile = (Projectile) (Object) this;
        Entity owner = projectile.getOwner();
        if (owner == null) {
            return;
        }
        if (entity instanceof LivingEntity living
                && (owner.isAlliedTo(living)
                || living.isAlliedTo(owner)
                || EntityUtil.shouldBlockOwnedSummonDamage(projectile, living))) {
            cir.setReturnValue(false);
        }
    }
}
