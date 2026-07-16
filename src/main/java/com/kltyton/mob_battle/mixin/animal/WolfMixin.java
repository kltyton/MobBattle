package com.kltyton.mob_battle.mixin.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public abstract class WolfMixin extends TamableAnimal {
    protected WolfMixin(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void mobBattle$targetCreepers(CallbackInfo ci) {
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(
                (Wolf) (Object) this,
                Creeper.class,
                10,
                true,
                false,
                (target, world) -> this.isTame() && !this.isOrderedToSit()
        ));
    }

    @Inject(method = "wantsToAttack", at = @At("HEAD"), cancellable = true)
    private void mobBattle$allowTameWolfToAttackCreeper(LivingEntity target, LivingEntity owner, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof Creeper && this.isTame() && owner != null) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void mobBattle$healTameWolf(CallbackInfo ci) {
        if (this.level() instanceof ServerLevel && this.isTame() && this.tickCount % (5 * 20) == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(1.0F);
        }
    }
}
