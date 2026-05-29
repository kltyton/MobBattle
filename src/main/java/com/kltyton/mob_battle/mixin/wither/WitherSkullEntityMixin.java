package com.kltyton.mob_battle.mixin.wither;

import com.kltyton.mob_battle.entity.enhancedwither.EnhancedWitherEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WitherSkull.class)
public abstract class WitherSkullEntityMixin {
    @ModifyArg(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 0), index = 2)
    private float enhancedWitherSkullMagicDamage(float original) {
        WitherSkull self = (WitherSkull) (Object) this;
        Entity owner = self.getOwner();
        if (owner instanceof EnhancedWitherEntity) {
            return self.isDangerous() ? 20.0F : 10.0F;
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
            return self.isDangerous() ? 3.0F : 2.0F;
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
