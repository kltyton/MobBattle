package com.kltyton.mob_battle.entity.voidcell;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

public class RandomFlyGoal extends Goal {
    public VoidCellEntity voidCell;
    public RandomFlyGoal(VoidCellEntity voidCell) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.voidCell = voidCell;
    }

    @Override
    public boolean canUse() {
        // 如果已经在移动或随机数未命中，则不开始新的移动
        return !voidCell.getMoveControl().hasWanted() && voidCell.getRandom().nextInt(reducedTickDelay(7)) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void tick() {
        BlockPos blockPos = voidCell.blockPosition();
        for (int i = 0; i < 3; i++) {
            BlockPos targetPos = blockPos.offset(
                    voidCell.getRandom().nextInt(15) - 7,
                    voidCell.getRandom().nextInt(11) - 5,
                    voidCell.getRandom().nextInt(15) - 7
            );

            // 增加限制条件：Y 必须大于 -220 且是空气
            if (targetPos.getY() > -220 && voidCell.level().isEmptyBlock(targetPos)) {
                voidCell.getMoveControl().setWantedPosition(
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
