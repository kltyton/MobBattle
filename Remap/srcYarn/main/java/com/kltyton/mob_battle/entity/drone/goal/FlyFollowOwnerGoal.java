package com.kltyton.mob_battle.entity.drone.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class FlyFollowOwnerGoal extends Goal {
    private final TameableEntity tameable;
    @Nullable
    private LivingEntity owner;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;

    public FlyFollowOwnerGoal(TameableEntity tameable, double speed, float minDistance, float maxDistance) {
        this.tameable = tameable;
        this.speed = speed;
        this.navigation = tameable.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        if (!(tameable.getNavigation() instanceof MobNavigation) && !(tameable.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity == null) {
            return false;
        } else if (this.tameable.cannotFollowOwner()) {
            return false;
        } else if (this.tameable.squaredDistanceTo(livingEntity) < this.minDistance * this.minDistance) {
            return false;
        } else {
            this.owner = livingEntity;
            return true;
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.navigation.isIdle()) {
            return false;
        } else {
            return !this.tameable.cannotFollowOwner() && !(this.tameable.squaredDistanceTo(this.owner) <= this.maxDistance * this.maxDistance);
        }
    }

    @Override
    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.tameable.getPathfindingPenalty(PathNodeType.WATER);
        this.tameable.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tameable.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    @Override
    public void tick() {
        boolean shouldTeleport = this.tameable.shouldTryTeleportToOwner();

        if (!shouldTeleport) {
            this.tameable.getLookControl().lookAt(this.owner, 10.0F, this.tameable.getMaxLookPitchChange());
        }

        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = this.getTickCount(10);

            if (shouldTeleport) {
                this.tameable.tryTeleportToOwner();
            } else {
                // === 关键修改：不要固定 +2.5，而是智能高度 ===
                BlockPos ground = getGroundY(this.owner.getBlockPos());
                double targetY = this.owner.getY() + 2.0; // 默认跟人差不多高

                if (ground != null) {
                    double idealY = ground.getY() + 6 + this.tameable.getRandom().nextInt(11); // 6~16
                    // 如果当前太低或太高，倾向飞向理想高度；否则保持和主人差不多高
                    if (this.tameable.getY() < ground.getY() + 5 || this.tameable.getY() > ground.getY() + 20) {
                        targetY = idealY;
                    }
                }

                this.navigation.startMovingTo(
                        this.owner.getX(),
                        targetY,
                        this.owner.getZ(),
                        this.speed
                );
            }
        }
    }
    private BlockPos getGroundY(BlockPos start) {
        BlockPos.Mutable mutable = start.mutableCopy().move(0, -1, 0);
        World world;
        if (this.owner != null) {
            world = this.owner.getWorld();
            for (int i = 0; i < 100; i++) {
                mutable.move(Direction.DOWN);
                BlockState state = world.getBlockState(mutable);
                if (state.isSolidBlock(world, mutable) && !state.isOf(Blocks.WATER)) {
                    return mutable.up(); // 返回可站立的地面顶部
                }
            }
        }
        return null;
    }
}
