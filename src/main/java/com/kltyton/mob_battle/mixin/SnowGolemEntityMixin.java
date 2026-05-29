package com.kltyton.mob_battle.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowGolem.class)
public abstract class SnowGolemEntityMixin extends AbstractGolem implements Shearable, RangedAttackMob {
    @Shadow public abstract boolean isSensitiveToWater();
    protected SnowGolemEntityMixin(EntityType<? extends AbstractGolem> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/AbstractGolem;aiStep()V", shift = At.Shift.AFTER), cancellable = true)
    public void tickMove(CallbackInfo ci) {
        if (!this.isSensitiveToWater()) {
            ci.cancel();
        }
    }
}
