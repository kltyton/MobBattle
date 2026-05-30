package com.kltyton.mob_battle.mixin;

import com.kltyton.mob_battle.event.DataTrackersEvent;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Warden.class)
public abstract class WardenEntityMixin extends Monster implements VibrationSystem {
    static {
        // 触发DataTrackers静态初始化
        boolean dummy = DataTrackersEvent.FORCED_ATTACK_FLAG != null;
    }

    @Shadow public abstract Brain<Warden> getBrain();

    protected WardenEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "canTargetEntity", at = @At("HEAD"), cancellable = true)
    private void forceValidTarget(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof Player player && player.isCreative())) {
            // 检查是否有强制攻击标志
            if (this.entityData.get(DataTrackersEvent.FORCED_ATTACK_FLAG)) {
                cir.setReturnValue(entity instanceof LivingEntity living &&
                        living.isAlive() &&
                        !living.isSpectator());
            }
        }
    }
    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void addCustomDataTracker(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DataTrackersEvent.FORCED_ATTACK_FLAG, false);
    }

}
