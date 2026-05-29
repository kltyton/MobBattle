package com.kltyton.mob_battle.entity.highbird.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

public class HABActiveTargetGoal<T extends LivingEntity> extends ActiveTargetGoal {

    public HABActiveTargetGoal(MobEntity mob, Class targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable TargetPredicate.EntityPredicate targetPredicate) {
        super(mob, targetClass, reciprocalChance, false, checkCanNavigate, targetPredicate);
    }
    @Override
    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            livingEntity = this.target;
        }

        if (livingEntity == null) {
            return false;
        } else if (!this.mob.canTarget(livingEntity)) {
            return false;
        } else {
            if (this.mob.isTeammate(livingEntity)) {
                return false;
            } else {
                double d = this.getFollowRange();
                if (this.mob.squaredDistanceTo(livingEntity) > d * d) {
                    return false;
                } else {
                    if (this.checkVisibility) {
                        if (this.mob.getVisibilityCache().canSee(livingEntity)) {
                            this.timeWithoutVisibility = 0;
                        } else if (++this.timeWithoutVisibility > toGoalTicks(this.maxTimeWithoutVisibility)) {
                            return false;
                        }
                    }

                    this.mob.setTarget(livingEntity);
                    return true;
                }
            }
        }
    }

}
