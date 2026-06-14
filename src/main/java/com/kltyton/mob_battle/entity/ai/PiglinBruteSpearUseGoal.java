package com.kltyton.mob_battle.entity.ai;

import com.kltyton.mob_battle.accessor.IPiglinBruteSpearMode;
import net.minecraft.world.entity.ai.goal.SpearUseGoal;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;

public class PiglinBruteSpearUseGoal extends SpearUseGoal<PiglinBrute> {
    private final PiglinBrute piglinBrute;

    public PiglinBruteSpearUseGoal(
            PiglinBrute piglinBrute,
            double speedModifierWhenCharging,
            double speedModifierWhenRepositioning,
            float approachDistance,
            float targetInRangeRadius
    ) {
        super(piglinBrute, speedModifierWhenCharging, speedModifierWhenRepositioning, approachDistance, targetInRangeRadius);
        this.piglinBrute = piglinBrute;
    }

    @Override
    public boolean canUse() {
        return ((IPiglinBruteSpearMode) this.piglinBrute).mobBattle$usesSpearAsItem() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return ((IPiglinBruteSpearMode) this.piglinBrute).mobBattle$usesSpearAsItem() && super.canContinueToUse();
    }
}
