package com.kltyton.mob_battle.mixin.entity.ai.goal;

import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GoalSelector.class)
public class GoalSelectorMixin {
    @Unique
    private int commonPriority = -1;

    @ModifyVariable(method = "add", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int normalizePriority(int priority, int originalPriority, Goal goal) {
        if (goal instanceof ActiveTargetGoal) {
            // 如果是该实例的第一个目标 AI，记录它的优先级作为“基准”
            if (commonPriority == -1) {
                commonPriority = priority;
            }
            return commonPriority;
        }
        return priority;
    }
}
