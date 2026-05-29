package com.kltyton.mob_battle.entity.drone.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FlyWanderAroundFarGoal extends RandomStrollGoal {
    private static final double MAX_HEIGHT = 319.0;  // 1.21 世界最高 319
    private static final double PREFERRED_MIN_Y = 80.0;  // 倾向保持在这个高度以上
    private static final double PREFERRED_MAX_Y = 200.0; // 不超过这个高度（可调）
    public static final float CHANCE = 0.001F;
    protected final float probability;

    public FlyWanderAroundFarGoal(PathfinderMob pathAwareEntity, double d) {
        this(pathAwareEntity, d, 0.001F);
    }

    public FlyWanderAroundFarGoal(PathfinderMob mob, double speed, float probability) {
        super(mob, speed);
        this.probability = probability;
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        // 1. 先找到实体脚下的固体地面高度
        BlockPos groundPos = this.getGroundY(this.mob.blockPosition());

        if (groundPos == null) {
            // 万一找不到地面（比如在虚空），就保持当前高度附近小范围移动
            return this.mob.position().add(
                    this.mob.getRandom().nextDouble() * 10 - 5,
                    this.mob.getRandom().nextDouble() * 4 - 2,
                    this.mob.getRandom().nextDouble() * 10 - 5
            );
        }

        // 2. 目标高度 = 地面高度 + 6~16 随机
        double targetY = groundPos.getY() + 6 + this.mob.getRandom().nextInt(11); // 6~16

        // 3. 在水平方向随机一个 8~20 格的点
        double angle = this.mob.getRandom().nextDouble() * Math.PI * 2.0;
        double distance = 8 + this.mob.getRandom().nextDouble() * 12; // 8~20 格

        double offsetX = Math.cos(angle) * distance;
        double offsetZ = Math.sin(angle) * distance;

        return new Vec3(
                this.mob.getX() + offsetX,
                targetY,
                this.mob.getZ() + offsetZ
        );
    }

    // 辅助方法：从当前坐标向下找固体方块，返回其顶部 Y（即地面高度）
    private BlockPos getGroundY(BlockPos start) {
        BlockPos.MutableBlockPos mutable = start.mutable().move(0, -1, 0);
        Level world = this.mob.level();

        // 向下最多找 100 格
        for (int i = 0; i < 100; i++) {
            mutable.move(Direction.DOWN);
            BlockState state = world.getBlockState(mutable);
            if (state.isRedstoneConductor(world, mutable) && !state.is(Blocks.WATER)) {
                return mutable.above(); // 返回可站立的地面顶部
            }
        }
        return null;
    }
}

