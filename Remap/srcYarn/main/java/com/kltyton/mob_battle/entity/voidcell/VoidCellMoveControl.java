package com.kltyton.mob_battle.entity.voidcell;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class VoidCellMoveControl extends MoveControl {
    public VoidCellMoveControl(VoidCellEntity owner) {
        super(owner);
    }

    @Override
    public void tick() {
        if (this.state == MoveControl.State.MOVE_TO) {
            Vec3d targetVec = new Vec3d(this.targetX - entity.getX(), this.targetY - entity.getY(), this.targetZ - entity.getZ());
            double distance = targetVec.length();

            // 如果距离非常近，停止移动
            if (distance < entity.getBoundingBox().getAverageSideLength()) {
                this.state = MoveControl.State.WAIT;
                entity.setVelocity(entity.getVelocity().multiply(0.5));
            } else {
                // 计算加速度
                entity.setVelocity(entity.getVelocity().add(targetVec.multiply(this.speed * 0.05 / distance)));

                // 平滑转向目标方向
                Vec3d velocity = entity.getVelocity();
                entity.setYaw(-((float) MathHelper.atan2(velocity.x, velocity.z)) * (180.0F / (float) Math.PI));
                entity.bodyYaw = entity.getYaw();
            }
        }
    }
}
