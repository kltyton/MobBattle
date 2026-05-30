package com.kltyton.mob_battle.entity.highbird.goals;

import com.kltyton.mob_battle.entity.highbird.HighbirdBaseEntity;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

public class MoveToNestForSleepGoal extends Goal {
    private final HighbirdAdulthoodEntity mob;
    private int cooldown;

    public MoveToNestForSleepGoal(HighbirdAdulthoodEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // 只在夜晚、未睡觉、有有效巢穴且不在巢穴附近时触发
        return mob.shouldSleep() &&
                !mob.isSleeping &&
                mob.hasNest() &&
                !mob.isNearNest() &&
                !mob.forcedWakeUp;
    }

    @Override
    public void start() {
        BlockPos nest = mob.getNestPos();
        if (nest != null) {
            // 尝试寻找路径到巢穴
            mob.getNavigation().moveTo(
                    nest.getX() + 0.5,
                    nest.getY(),
                    nest.getZ() + 0.5,
                    1.0 // 移动速度
            );
        }
    }

    @Override
    public boolean canContinueToUse() {
        // 继续执行直到到达巢穴附近或开始睡觉
        return !mob.isNearNest() &&
                !mob.isSleeping &&
                mob.hasNest() &&
                mob.shouldSleep() &&
                !mob.forcedWakeUp;
    }

    @Override
    public void stop() {
        // 到达巢穴附近后开始睡觉
        if (mob.isNearNest()) {
            mob.startSleeping();
        }
        mob.getNavigation().stop();
    }
}
