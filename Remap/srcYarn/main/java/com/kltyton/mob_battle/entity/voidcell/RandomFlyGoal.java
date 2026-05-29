package com.kltyton.mob_battle.entity.voidcell;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class RandomFlyGoal extends Goal {
    public VoidCellEntity voidCell;
    public RandomFlyGoal(VoidCellEntity voidCell) {
        this.setControls(EnumSet.of(Goal.Control.MOVE));
        this.voidCell = voidCell;
    }

    @Override
    public boolean canStart() {
        // 如果已经在移动或随机数未命中，则不开始新的移动
        return !voidCell.getMoveControl().isMoving() && voidCell.getRandom().nextInt(toGoalTicks(7)) == 0;
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }

    @Override
    public void tick() {
        BlockPos blockPos = voidCell.getBlockPos();
        for (int i = 0; i < 3; i++) {
            BlockPos targetPos = blockPos.add(
                    voidCell.getRandom().nextInt(15) - 7,
                    voidCell.getRandom().nextInt(11) - 5,
                    voidCell.getRandom().nextInt(15) - 7
            );

            // 增加限制条件：Y 必须大于 -220 且是空气
            if (targetPos.getY() > -220 && voidCell.getWorld().isAir(targetPos)) {
                voidCell.getMoveControl().moveTo(
                        targetPos.getX() + 0.5,
                        targetPos.getY() + 0.5,
                        targetPos.getZ() + 0.5,
                        0.25
                );
                break;
            }
        }
    }
}
