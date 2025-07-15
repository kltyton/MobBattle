package com.kltyton.mob_battle.entity.xunsheng;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class XunShengAttackGoal extends MeleeAttackGoal {
    private final XunShengEntity xunsheng;
    private int ticks;

    public XunShengAttackGoal(XunShengEntity xunsheng, double speed, boolean pauseWhenMobIdle) {
        super(xunsheng, speed, pauseWhenMobIdle);
        this.xunsheng = xunsheng;
    }

    @Override
    public void start() {
        super.start();
        this.ticks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.xunsheng.setAttacking(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.ticks++;
        if (this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2) {
            this.xunsheng.setAttacking(true);
        } else {
            this.xunsheng.setAttacking(false);
        }
    }
}

