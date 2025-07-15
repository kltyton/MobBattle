package com.kltyton.mob_battle.mixin;

import com.kltyton.mob_battle.event.DataTrackers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.event.Vibrations;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WardenEntity.class)
public abstract class WardenEntityMixin extends HostileEntity implements Vibrations {
    static {
        // 触发DataTrackers静态初始化
        boolean dummy = DataTrackers.FORCED_ATTACK_FLAG != null;
    }
    @Shadow public abstract void updateAttackTarget(LivingEntity target);


    @Shadow public abstract void increaseAngerAt(@Nullable Entity entity, int amount, boolean listening);

    @Shadow public abstract Brain<WardenEntity> getBrain();

    protected WardenEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "isValidTarget", at = @At("HEAD"), cancellable = true)
    private void forceValidTarget(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof PlayerEntity player && player.isCreative())) {
            // 检查是否有强制攻击标志
            if (this.dataTracker.get(DataTrackers.FORCED_ATTACK_FLAG)) {
                cir.setReturnValue(entity instanceof LivingEntity living &&
                        living.isAlive() &&
                        !living.isSpectator());
            }
        }
    }
    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void addCustomDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(DataTrackers.FORCED_ATTACK_FLAG, false);
    }

}
