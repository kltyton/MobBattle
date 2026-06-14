package com.kltyton.mob_battle.mixin;

import com.kltyton.mob_battle.entity.snowgolem.NewSnowGolemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowGolem.class)
public abstract class SnowGolemEntityMixin extends AbstractGolem implements Shearable, RangedAttackMob {
    @Shadow public abstract boolean isSensitiveToWater();
    protected SnowGolemEntityMixin(EntityType<? extends AbstractGolem> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/golem/AbstractGolem;aiStep()V", shift = At.Shift.AFTER), cancellable = true)
    public void tickMove(CallbackInfo ci) {
        if (!this.isSensitiveToWater()) {
            ci.cancel();
        }
    }

    @Redirect(
            method = "aiStep",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private boolean mobBattle$preventNewSnowGolemSnowTrail(Level level, BlockPos pos, BlockState state) {
        if ((Object) this instanceof NewSnowGolemEntity) {
            return false;
        }
        return level.setBlockAndUpdate(pos, state);
    }
}
