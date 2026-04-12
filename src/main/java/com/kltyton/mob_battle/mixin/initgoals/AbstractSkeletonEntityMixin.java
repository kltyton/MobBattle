package com.kltyton.mob_battle.mixin.initgoals;

import com.kltyton.mob_battle.entity.skull.IModSkullEntity;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSkeletonEntity.class)
public class AbstractSkeletonEntityMixin {
    @WrapWithCondition(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 2))
    private boolean initGoals(GoalSelector instance, int priority, Goal goal) {
        return !(this instanceof IModSkullEntity);
    }
}
