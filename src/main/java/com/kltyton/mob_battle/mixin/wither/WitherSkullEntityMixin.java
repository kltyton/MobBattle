package com.kltyton.mob_battle.mixin.wither;

import com.kltyton.mob_battle.entity.enhancedwither.EnhancedWitherEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WitherSkullEntity.class)
public abstract class WitherSkullEntityMixin {
    @ModifyArg(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 0), index = 2)
    private float enhancedWitherSkullMagicDamage(float original) {
        WitherSkullEntity self = (WitherSkullEntity) (Object) this;
        Entity owner = self.getOwner();
        if (owner instanceof EnhancedWitherEntity) {
            return self.isCharged() ? 20.0F : 10.0F;
        }
        return original;
    }

    @ModifyArg(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZLnet/minecraft/world/World$ExplosionSourceType;)V"), index = 5)
    private boolean enhancedWitherSkullNoFire(boolean createFire) {
        WitherSkullEntity self = (WitherSkullEntity) (Object) this;
        return !(self.getOwner() instanceof EnhancedWitherEntity) && createFire;
    }

    @ModifyArg(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZLnet/minecraft/world/World$ExplosionSourceType;)V"), index = 4)
    private float enhancedWitherSkullExplosionDamage(float power) {
        WitherSkullEntity self = (WitherSkullEntity) (Object) this;
        Entity owner = self.getOwner();
        if (owner instanceof EnhancedWitherEntity) {
            return self.isCharged() ? 3.0F : 2.0F;
        }
        return power;
    }

    @ModifyArg(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZLnet/minecraft/world/World$ExplosionSourceType;)V"), index = 6)
    private World.ExplosionSourceType enhancedWitherSkullNoBlockBreak(World.ExplosionSourceType explosionSourceType) {
        WitherSkullEntity self = (WitherSkullEntity) (Object) this;
        Entity owner = self.getOwner();
        if (owner instanceof EnhancedWitherEntity) {
            return World.ExplosionSourceType.NONE;
        }
        return explosionSourceType;
    }
}
