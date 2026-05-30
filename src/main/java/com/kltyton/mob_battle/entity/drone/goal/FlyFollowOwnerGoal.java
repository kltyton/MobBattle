package com.kltyton.mob_battle.entity.drone.goal;

import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;

public class FlyFollowOwnerGoal extends Goal {
    private final TamableAnimal tameable;
    @Nullable
    private LivingEntity owner;
    private final double speed;
    private final PathNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;

    public FlyFollowOwnerGoal(TamableAnimal tameable, double speed, float minDistance, float maxDistance) {
        this.tameable = tameable;
        this.speed = speed;
        this.navigation = tameable.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(tameable.getNavigation() instanceof GroundPathNavigation) && !(tameable.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity == null) {
            return false;
        } else if (this.tameable.unableToMoveToOwner()) {
            return false;
        } else if (this.tameable.distanceToSqr(livingEntity) < this.minDistance * this.minDistance) {
            return false;
        } else {
            this.owner = livingEntity;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else {
            return !this.tameable.unableToMoveToOwner() && !(this.tameable.distanceToSqr(this.owner) <= this.maxDistance * this.maxDistance);
        }
    }

    @Override
    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.tameable.getPathfindingMalus(PathType.WATER);
        this.tameable.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tameable.setPathfindingMalus(PathType.WATER, this.oldWaterPathfindingPenalty);
    }

    @Override
    public void tick() {
        boolean shouldTeleport = this.tameable.shouldTryTeleportToOwner();

        if (!shouldTeleport) {
            this.tameable.getLookControl().setLookAt(this.owner, 10.0F, this.tameable.getMaxHeadXRot());
        }

        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = this.adjustedTickDelay(10);

            if (shouldTeleport) {
                this.tameable.tryToTeleportToOwner();
            } else {
                // === 关键修改：不要固定 +2.5，而是智能高度 ===
                BlockPos ground = getGroundY(this.owner.blockPosition());
                double targetY = this.owner.getY() + 2.0; // 默认跟人差不多高

                if (ground != null) {
                    double idealY = ground.getY() + 6 + this.tameable.getRandom().nextInt(11); // 6~16
                    // 如果当前太低或太高，倾向飞向理想高度；否则保持和主人差不多高
                    if (this.tameable.getY() < ground.getY() + 5 || this.tameable.getY() > ground.getY() + 20) {
                        targetY = idealY;
                    }
                }

                this.navigation.moveTo(
                        this.owner.getX(),
                        targetY,
                        this.owner.getZ(),
                        this.speed
                );
            }
        }
    }
    private BlockPos getGroundY(BlockPos start) {
        BlockPos.MutableBlockPos mutable = start.mutable().move(0, -1, 0);
        Level world;
        if (this.owner != null) {
            world = this.owner.level();
            for (int i = 0; i < 100; i++) {
                mutable.move(Direction.DOWN);
                BlockState state = world.getBlockState(mutable);
                if (state.isRedstoneConductor(world, mutable) && !state.is(Blocks.WATER)) {
                    return mutable.above(); // 返回可站立的地面顶部
                }
            }
        }
        return null;
    }
}
