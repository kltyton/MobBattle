package com.kltyton.mob_battle.entity.highbird.goals;

import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

import java.util.EnumSet;

public class SmartWanderGoal extends Goal {
    private final HighbirdAdulthoodEntity mob;
    private final double speed;
    private final double nestRadius;
    private final double nestCenterProbability;

    public SmartWanderGoal(HighbirdAdulthoodEntity mob, double speed, double nestRadius, double nestCenterProbability) {
        this.mob = mob;
        this.speed = speed;
        this.nestRadius = nestRadius;
        this.nestCenterProbability = nestCenterProbability;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return mob.getNavigation().isIdle() && mob.getRandom().nextInt(10) == 0;
    }

    @Override
    public boolean shouldContinue() {
        return mob.getNavigation().isFollowingPath();
    }

    @Override
    public void start() {
        Vec3d targetPos = null;

        // 如果有有效巢穴，优先在巢穴附近游荡
        if (mob.isNestValid()) {
            // 80%概率在巢穴半径内游荡，20%概率直接靠近巢穴
            if (mob.getRandom().nextDouble() < nestCenterProbability) {
                targetPos = Vec3d.ofCenter(mob.getNestPos());
            } else {
                targetPos = getRandomPosAroundNest();
            }
        }

        // 没有巢穴则自由游荡
        if (targetPos == null) {
            targetPos = getRandomLandPos();
        }

        // 如果找到有效位置则移动
        if (targetPos != null) {
            mob.getNavigation().startMovingAlong(
                    mob.getNavigation().findPathTo(
                            BlockPos.ofFloored(targetPos),
                            1
                    ),
                    speed
            );
        }
    }

    // 获取巢穴附近的随机位置
    private Vec3d getRandomPosAroundNest() {
        BlockPos nest = mob.getNestPos();
        double angle = mob.getRandom().nextDouble() * Math.PI * 2;
        double radius = nestRadius * Math.sqrt(mob.getRandom().nextDouble());

        double x = nest.getX() + 0.5 + Math.cos(angle) * radius;
        double z = nest.getZ() + 0.5 + Math.sin(angle) * radius;
        double y = mob.getWorld().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z);

        return new Vec3d(x, y, z);
    }

    // 获取普通游荡位置（使用正确的FuzzyTargeting方法）
    private Vec3d getRandomLandPos() {
        // 使用正确的参数调用FuzzyTargeting.find()
        return FuzzyTargeting.find(mob, 10, 7); // 水平范围10，垂直范围7
    }
}
