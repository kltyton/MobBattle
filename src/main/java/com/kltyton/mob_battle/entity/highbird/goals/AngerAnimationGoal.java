package com.kltyton.mob_battle.entity.highbird.goals;

import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class AngerAnimationGoal extends Goal {
    private final HighbirdAdulthoodEntity bird;
    private int timer;

    public AngerAnimationGoal(HighbirdAdulthoodEntity bird) {
        this.bird = bird;
        setControls(EnumSet.of(Control.MOVE, Goal.Control.LOOK));
    }
    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }
    @Override
    public boolean canStart() {
        LivingEntity target = bird.getTarget();
        if (target == null || !target.isAlive()) return false;

        // 只有第一次进入“有目标”状态时才触发
        return !bird.angerTriggered;
    }

    @Override
    public void start() {
        bird.angerTriggered = true;
        bird.triggerAnim("anger_controller", "anger"); // 这里对应你在 Geo 里定义的动画名
        bird.getNavigation().stop();                     // 确保停下来
        timer = 40;                                      // 40 tick ≈ 2 秒，可按动画长度改
    }

    @Override
    public void tick() {
        if (timer > 0) {
            timer--;

            // 每 tick 把头部转向当前目标
            LivingEntity target = bird.getTarget();
            if (target != null) {
                bird.getLookControl().lookAt(target, 90.0F, 90.0F);
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        return timer > 0;
    }

    @Override
    public void stop() {
        // 动画播完，允许正常移动 / 攻击
    }
}
