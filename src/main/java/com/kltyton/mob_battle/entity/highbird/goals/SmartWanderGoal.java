package com.kltyton.mob_battle.entity.highbird.goals;

import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

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
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return mob.getNavigation().isDone() && mob.getRandom().nextInt(10) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.getNavigation().isInProgress();
    }

    @Override
    public void start() {
        Vec3 targetPos = null;

        // 如果有有效巢穴，优先在巢穴附近游荡
        if (mob.isNestValid()) {
            // 80%概率在巢穴半径内游荡，20%概率直接靠近巢穴
            if (mob.getRandom().nextDouble() < nestCenterProbability) {
                targetPos = Vec3.atCenterOf(mob.getNestPos());
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
            mob.getNavigation().moveTo(
                    mob.getNavigation().createPath(
                            BlockPos.containing(targetPos),
                            1
                    ),
                    speed
            );
        }
    }

    // 获取巢穴附近的随机位置
    private Vec3 getRandomPosAroundNest() {
        BlockPos nest = mob.getNestPos();
        double angle = mob.getRandom().nextDouble() * Math.PI * 2;
        double radius = nestRadius * Math.sqrt(mob.getRandom().nextDouble());

        double x = nest.getX() + 0.5 + Math.cos(angle) * radius;
        double z = nest.getZ() + 0.5 + Math.sin(angle) * radius;
        double y = mob.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z);

        return new Vec3(x, y, z);
    }

    // 获取普通游荡位置（使用正确的FuzzyTargeting方法）
    private Vec3 getRandomLandPos() {
        // 使用正确的参数调用FuzzyTargeting.find()
        return LandRandomPos.getPos(mob, 10, 7); // 水平范围10，垂直范围7
    }
}
