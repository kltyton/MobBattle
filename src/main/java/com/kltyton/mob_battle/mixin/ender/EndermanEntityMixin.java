package com.kltyton.mob_battle.mixin.ender;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public abstract class EndermanEntityMixin extends Monster {
    protected EndermanEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addEndPlayerTargetGoal(CallbackInfo ci) {
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                (player, world) -> this.level().dimension() == Level.END));
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void doNotTargetEnderDragon(@Nullable LivingEntity target, CallbackInfo ci) {
        if (target instanceof EnderDragon) {
            super.setTarget(null);
            ci.cancel();
        }
    }
}
