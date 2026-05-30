package com.kltyton.mob_battle.entity.highbird.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.Nullable;

public class HABActiveTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal {
    private int mobBattle$unseenTicks;

    public HABActiveTargetGoal(Mob mob, Class targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable TargetingConditions.Selector targetPredicate) {
        super(mob, targetClass, reciprocalChance, false, checkCanNavigate, targetPredicate);
    }

    @Override
    public void start() {
        super.start();
        this.mobBattle$unseenTicks = 0;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            livingEntity = this.targetMob;
        }

        if (livingEntity == null) {
            return false;
        } else if (!this.mob.canAttack(livingEntity)) {
            return false;
        } else {
            if (this.mob.isAlliedTo(livingEntity)) {
                return false;
            } else {
                double d = this.getFollowDistance();
                if (this.mob.distanceToSqr(livingEntity) > d * d) {
                    return false;
                } else {
                    if (this.mustSee) {
                        if (this.mob.getSensing().hasLineOfSight(livingEntity)) {
                            this.mobBattle$unseenTicks = 0;
                        } else if (++this.mobBattle$unseenTicks > reducedTickDelay(this.unseenMemoryTicks)) {
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
