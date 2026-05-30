package com.kltyton.mob_battle.entity.voidcell;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class VoidCellMoveControl extends MoveControl {
    public VoidCellMoveControl(VoidCellEntity owner) {
        super(owner);
    }

    @Override
    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO) {
            Vec3 targetVec = new Vec3(this.wantedX - mob.getX(), this.wantedY - mob.getY(), this.wantedZ - mob.getZ());
            double distance = targetVec.length();

            // 如果距离非常近，停止移动
            if (distance < mob.getBoundingBox().getSize()) {
                this.operation = MoveControl.Operation.WAIT;
                mob.setDeltaMovement(mob.getDeltaMovement().scale(0.5));
            } else {
                // 计算加速度
                mob.setDeltaMovement(mob.getDeltaMovement().add(targetVec.scale(this.speedModifier * 0.05 / distance)));

                // 平滑转向目标方向
                Vec3 velocity = mob.getDeltaMovement();
                mob.setYRot(-((float) Mth.atan2(velocity.x, velocity.z)) * (180.0F / (float) Math.PI));
                mob.yBodyRot = mob.getYRot();
            }
        }
    }
}
