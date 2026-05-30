package com.kltyton.mob_battle.entity.highbird.goals;

import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class FindNestGoal extends Goal {
    private final HighbirdAdulthoodEntity mob;
    private int cooldown;

    public FindNestGoal(HighbirdAdulthoodEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
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
        double range = mob.getAttributeValue(Attributes.FOLLOW_RANGE);
        List<BlockPos> hayBlocks = BlockPos.withinManhattanStream(
                        BlockPos.containing(mob.position()),
                        (int) range, 3, (int) range
                )
                .filter(pos -> mob.level().getBlockState(pos).is(Blocks.HAY_BLOCK))
                .map(BlockPos::immutable)
                .toList();

        // 找到最近的干草块设置为巢穴
        if (!hayBlocks.isEmpty()) {
            hayBlocks.stream()
                    .min((a, b) -> (int) (mob.distanceToSqr(Vec3.atLowerCornerOf(a)) - mob.distanceToSqr(Vec3.atLowerCornerOf(b)))).ifPresent(mob::setNestPos);

        }
    }
}
