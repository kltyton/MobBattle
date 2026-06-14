package com.kltyton.mob_battle.entity.ai;

import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.Items;

public class ZombieBowAttackGoal<T extends Monster & RangedAttackMob> extends RangedBowAttackGoal<T> {
    private final T mob;

    public ZombieBowAttackGoal(T mob, double speedModifier, int attackIntervalMin, float attackRadius) {
        super(mob, speedModifier, attackIntervalMin, attackRadius);
        this.mob = mob;
    }

    @Override
    protected boolean isHoldingBow() {
        return this.mob.getMainHandItem().is(Items.BOW)
                && this.mob.canUseNonMeleeWeapon(this.mob.getMainHandItem());
    }
}
