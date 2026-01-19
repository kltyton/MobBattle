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
        if (this.state == State.STRAFE) {
            // STRAFE 状态保持原版逻辑不变
            float f = (float)this.entity.getAttributeValue(EntityAttributes.MOVEMENT_SPEED);
            float g = (float)this.speed * f;
            float h = this.forwardMovement;
            float i = this.sidewaysMovement;
            float j = MathHelper.sqrt(h * h + i * i);
            if (j < 1.0F) {
                j = 1.0F;
            }
            j = g / j;
            h *= j;
            i *= j;
            float k = MathHelper.sin(this.entity.getYaw() * (float) (Math.PI / 180.0));
            float l = MathHelper.cos(this.entity.getYaw() * (float) (Math.PI / 180.0));
            float m = h * l - i * k;
            float n = i * l + h * k;
            if (!this.isPosWalkable(m, n)) {
                this.forwardMovement = 1.0F;
                this.sidewaysMovement = 0.0F;
            }
            this.entity.setMovementSpeed(g);
            this.entity.setForwardSpeed(this.forwardMovement);
            this.entity.setSidewaysSpeed(this.sidewaysMovement);
            this.state = State.WAIT;
        } else if (this.state == State.MOVE_TO) {
            this.state = State.WAIT;
            double d = this.targetX - this.entity.getX();
            double e = this.targetZ - this.entity.getZ();
            double o = this.targetY - this.entity.getY();
            double p = d * d + o * o + e * e;
            if (p < 2.5000003E-7F) {
                this.entity.setForwardSpeed(0.0F);
                return;
            }
            float targetYaw = (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F;

            // 关键修改：把原版的 90.0F 改成 MAX_TURN_ANGLE
            this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), targetYaw, 5.0F));

            this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.MOVEMENT_SPEED)));

            BlockPos blockPos = this.entity.getBlockPos();
            BlockState blockState = this.entity.getWorld().getBlockState(blockPos);
            VoxelShape voxelShape = blockState.getCollisionShape(this.entity.getWorld(), blockPos);
            if (o > this.entity.getStepHeight() && d * d + e * e < Math.max(1.0F, this.entity.getWidth())
                    || !voxelShape.isEmpty()
                    && this.entity.getY() < voxelShape.getMax(Direction.Axis.Y) + (double)blockPos.getY()
                    && !blockState.isIn(BlockTags.DOORS)
                    && !blockState.isIn(BlockTags.FENCES)) {
                this.entity.getJumpControl().setActive();
                this.state = State.JUMPING;
            }
        } else if (this.state == State.JUMPING) {
            this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.MOVEMENT_SPEED)));
            if (this.entity.isOnGround() || this.entity.isInFluid() && this.entity.shouldSwimInFluids()) {
                this.state = State.WAIT;
            }
        } else {
            this.entity.setForwardSpeed(0.0F);
        }
    }
}
