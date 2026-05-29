package com.kltyton.mob_battle.entity.highbird.goals;

import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

public class FindNestGoal extends Goal {
    private final HighbirdAdulthoodEntity mob;
    private int cooldown;

    public FindNestGoal(HighbirdAdulthoodEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        // 冷却时间检查
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        // 已有有效巢穴时不执行
        if (mob.isNestValid()) {
            return false;
        }

        // 设置下次检查的冷却时间
        cooldown = HighbirdAdulthoodEntity.HAY_BLOCK_CHECK_INTERVAL;
        return true;
    }

    @Override
    public void start() {
        // 搜索索敌范围内的干草块
        double range = mob.getAttributeValue(EntityAttributes.FOLLOW_RANGE);
        List<BlockPos> hayBlocks = BlockPos.streamOutwards(
                        BlockPos.ofFloored(mob.getPos()),
                        (int) range, 3, (int) range
                )
                .filter(pos -> mob.getWorld().getBlockState(pos).isOf(Blocks.HAY_BLOCK))
                .map(BlockPos::toImmutable)
                .toList();

        // 找到最近的干草块设置为巢穴
        if (!hayBlocks.isEmpty()) {
            hayBlocks.stream()
                    .min((a, b) -> (int) (mob.squaredDistanceTo(Vec3d.of(a)) - mob.squaredDistanceTo(Vec3d.of(b)))).ifPresent(mob::setNestPos);

        }
    }
}
