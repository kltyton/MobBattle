package com.kltyton.mob_battle.entity.highbird.goals;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class HighbirdMeleeAttackGoal extends MeleeAttackGoal {
    private static final int ATTACK_INTERVAL_TICKS = 40;   // 2 秒

    public HighbirdMeleeAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
    }

    //冷却时间
    @Override
    protected void resetCooldown() {
        this.cooldown = this.getTickCount(ATTACK_INTERVAL_TICKS);
    }

    @Override
    protected int getMaxCooldown() {
        return this.getTickCount(ATTACK_INTERVAL_TICKS);
    }
}
