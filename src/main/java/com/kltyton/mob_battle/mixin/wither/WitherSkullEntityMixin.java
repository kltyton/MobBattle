package com.kltyton.mob_battle.mixin.wither;

import com.kltyton.mob_battle.entity.enhancedwither.EnhancedWitherEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherSkull.class)
public abstract class WitherSkullEntityMixin {
    @Redirect(
            method = "onHitEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V")
    )
    private void mobBattle$removeWitherSkullKillHeal(LivingEntity owner, float amount) {
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
    private void enhancedWitherSkullSkipFriendlyTarget(EntityHitResult hitResult, CallbackInfo ci) {
        WitherSkull self = (WitherSkull) (Object) this;
        Entity owner = self.getOwner();
        if (owner instanceof EnhancedWitherEntity
                && hitResult.getEntity() instanceof LivingEntity target
                && !EntityUtil.isValidSummonCombatTarget(self, owner, target)) {
            ci.cancel();
        }
    }

    @Inject(method = "onHit", at = @At("HEAD"))
    private void enhancedWitherSkullManualDamage(HitResult hitResult, CallbackInfo ci) {
        WitherSkull self = (WitherSkull) (Object) this;
        Entity owner = self.getOwner();
        if (!(owner instanceof EnhancedWitherEntity) || !(self.level() instanceof ServerLevel world)) {
            return;
        }

        LivingEntity livingOwner = owner instanceof LivingEntity living ? living : null;
        AABB damageBox = self.getBoundingBox().inflate(3.0D);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, damageBox,
                target -> target.distanceToSqr(self) <= 9.0D
                        && EntityUtil.isValidSummonCombatTarget(self, owner, target))) {
            int oldInvulnerableTime = target.invulnerableTime;
            boolean hit = target.hurtServer(world, self.damageSources().explosion(self, livingOwner), 40.0F);
            int postExplosionInvulnerableTime = target.invulnerableTime;
            if (self.isDangerous() && target.isAlive()) {
                target.invulnerableTime = 0;
                target.hurtServer(world, self.damageSources().indirectMagic(self, livingOwner), 12.0F);
                target.invulnerableTime = Math.max(target.invulnerableTime, Math.max(oldInvulnerableTime, postExplosionInvulnerableTime));
            } else if (hit) {
                target.invulnerableTime = Math.max(target.invulnerableTime, oldInvulnerableTime);
            }
        }
    }

    @ModifyArg(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 0), index = 2)
    private float enhancedWitherSkullMagicDamage(float original) {
        WitherSkull self = (WitherSkull) (Object) this;
        Entity owner = self.getOwner();
        if (owner instanceof EnhancedWitherEntity) {
            return 0.0F;
        }
        return original;
    }

    @ModifyArg(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)V"), index = 5)
    private boolean enhancedWitherSkullNoFire(boolean createFire) {
        WitherSkull self = (WitherSkull) (Object) this;
        return !(self.getOwner() instanceof EnhancedWitherEntity) && createFire;
    }

    @ModifyArg(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)V"), index = 4)
    private float enhancedWitherSkullExplosionDamage(float power) {
        WitherSkull self = (WitherSkull) (Object) this;
        Entity owner = self.getOwner();
        if (owner instanceof EnhancedWitherEntity) {
            return 0.0F;
        }
        return power;
    }

    @ModifyArg(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)V"), index = 6)
    private Level.ExplosionInteraction enhancedWitherSkullNoBlockBreak(Level.ExplosionInteraction explosionSourceType) {
        WitherSkull self = (WitherSkull) (Object) this;
        Entity owner = self.getOwner();
        if (owner instanceof EnhancedWitherEntity) {
            return Level.ExplosionInteraction.NONE;
        }
        return explosionSourceType;
    }
}
