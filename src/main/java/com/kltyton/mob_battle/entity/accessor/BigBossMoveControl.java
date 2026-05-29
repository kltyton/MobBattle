package com.kltyton.mob_battle.entity.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BigBossMoveControl extends MoveControl {
    public BigBossMoveControl(Mob entity) {
        super(entity);
    }

    @Override
    public void tick() {
        if (this.operation == Operation.MOVE_TO) {
            this.operation = Operation.WAIT;
            double dx = this.wantedX - this.mob.getX();
            double dz = this.wantedZ - this.mob.getZ();
            double dy = this.wantedY - this.mob.getY();
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq < 2.5E-7) {
                this.mob.setZza(0.0F);
                return;
            }

            // 计算目标角度
            float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;

            // 计算当前角度与目标的差值 (-180 到 180)
            float angleDiff = Mth.wrapDegrees(targetYaw - this.mob.getYRot());

            // 1. 动态旋转速度：角度越大转得越快，但最大给到 30-50 度左右
            // 原版的 5.0F 太小了，建议 Boss 类实体给到 10.0F - 30.0F
            float maxTurnSpeed = 20.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), targetYaw, maxTurnSpeed));

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

            float baseSpeed = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
            this.mob.setSpeed(baseSpeed * movementMultiplier);

            BlockPos blockPos = this.mob.blockPosition();
            BlockState blockState = this.mob.level().getBlockState(blockPos);
            VoxelShape voxelShape = blockState.getCollisionShape(this.mob.level(), blockPos);
            if (dy > this.mob.maxUpStep() && dx * dx + dz * dz < Math.max(1.0F, this.mob.getBbWidth())
                    || !voxelShape.isEmpty()
                    && this.mob.getY() < voxelShape.max(Direction.Axis.Y) + blockPos.getY()
                    && !blockState.is(BlockTags.DOORS)
                    && !blockState.is(BlockTags.FENCES)) {
                this.mob.getJumpControl().jump();
                this.operation = MoveControl.Operation.JUMPING;
            }
        } else if (this.operation == MoveControl.Operation.STRAFE) {
            super.tick();
        } else if (this.operation == MoveControl.Operation.JUMPING) {
            this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            if (this.mob.onGround() || this.mob.isInLiquid() && this.mob.isAffectedByFluids()) {
                this.operation = MoveControl.Operation.WAIT;
            }
        } else {
            this.mob.setZza(0.0F);
        }
    }
}