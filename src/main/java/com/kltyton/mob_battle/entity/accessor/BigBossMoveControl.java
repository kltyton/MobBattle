package com.kltyton.mob_battle.entity.accessor;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

public class BigBossMoveControl extends MoveControl {
    public BigBossMoveControl(MobEntity entity) {
        super(entity);
    }

    @Override
    public void tick() {
        if (this.state == State.MOVE_TO) {
            this.state = State.WAIT;
            double dx = this.targetX - this.entity.getX();
            double dz = this.targetZ - this.entity.getZ();
            double dy = this.targetY - this.entity.getY();
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq < 2.5E-7) {
                this.entity.setForwardSpeed(0.0F);
                return;
            }

            // 计算目标角度
            float targetYaw = (float) (MathHelper.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;

            // 计算当前角度与目标的差值 (-180 到 180)
            float angleDiff = MathHelper.wrapDegrees(targetYaw - this.entity.getYaw());

            // 1. 动态旋转速度：角度越大转得越快，但最大给到 30-50 度左右
            // 原版的 5.0F 太小了，建议 Boss 类实体给到 10.0F - 30.0F
            float maxTurnSpeed = 20.0F;
            this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), targetYaw, maxTurnSpeed));

            // 2. 速度惩罚逻辑：转弯半径缩小的关键
            float movementMultiplier = 1.0F;
            float absDiff = Math.abs(angleDiff);

            if (absDiff > 70.0F) {
                // 角度差太大，几乎停止移动，原地转向
                movementMultiplier = 0.1F;
            } else if (absDiff > 30.0F) {
                // 中等角度转向，速度减半
                movementMultiplier = 0.5F;
            }

            float baseSpeed = (float) (this.speed * this.entity.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));
            this.entity.setMovementSpeed(baseSpeed * movementMultiplier);

            BlockPos blockPos = this.entity.getBlockPos();
            BlockState blockState = this.entity.getWorld().getBlockState(blockPos);
            VoxelShape voxelShape = blockState.getCollisionShape(this.entity.getWorld(), blockPos);
            if (dy > this.entity.getStepHeight() && dx * dx + dz * dz < Math.max(1.0F, this.entity.getWidth())
                    || !voxelShape.isEmpty()
                    && this.entity.getY() < voxelShape.getMax(Direction.Axis.Y) + blockPos.getY()
                    && !blockState.isIn(BlockTags.DOORS)
                    && !blockState.isIn(BlockTags.FENCES)) {
                this.entity.getJumpControl().setActive();
                this.state = MoveControl.State.JUMPING;
            }
        } else if (this.state == MoveControl.State.STRAFE) {
            super.tick();
        } else if (this.state == MoveControl.State.JUMPING) {
            this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.MOVEMENT_SPEED)));
            if (this.entity.isOnGround() || this.entity.isInFluid() && this.entity.shouldSwimInFluids()) {
                this.state = MoveControl.State.WAIT;
            }
        } else {
            this.entity.setForwardSpeed(0.0F);
        }
    }
}