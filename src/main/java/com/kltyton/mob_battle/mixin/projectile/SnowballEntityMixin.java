package com.kltyton.mob_battle.mixin.projectile;

import com.kltyton.mob_battle.entity.snowgolem.NewSnowGolemEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Snowball.class)
public abstract class SnowballEntityMixin {
    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
    private void mobBattle$newSnowGolemDamage(EntityHitResult hitResult, CallbackInfo ci) {
        Snowball snowball = (Snowball) (Object) this;
        if (!(snowball.getOwner() instanceof NewSnowGolemEntity) || !(snowball.level() instanceof ServerLevel world)) {
            return;
        }

        Entity target = hitResult.getEntity();
        if (target instanceof LivingEntity livingTarget) {
            livingTarget.invulnerableTime = 0;
        }
        target.hurtServer(world, snowball.damageSources().thrown(snowball, snowball.getOwner()), 1.0F);
        if (target instanceof LivingEntity livingTarget) {
            livingTarget.invulnerableTime = 0;
        }
        ci.cancel();
    }
}
