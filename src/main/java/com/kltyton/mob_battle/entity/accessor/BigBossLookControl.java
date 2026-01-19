package com.kltyton.mob_battle.entity.accessor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.mob.MobEntity;

public class BigBossLookControl extends LookControl {
    public BigBossLookControl(MobEntity entity) {
        super(entity);
    }

    @Override
    public void lookAt(Entity entity, float maxYawChange, float maxPitchChange) {
        super.lookAt(entity, 8.0F, 8.0F);  // 强制小角度
    }

    @Override
    public void lookAt(double x, double y, double z, float maxYawChange, float maxPitchChange) {
        super.lookAt(x, y, z, 8.0F, 8.0F);
    }

    @Override
    public void tick() {
        super.tick();
        // 可选：把无目标时对齐 bodyYaw 的 10.0F 也改小
        if (this.lookAtTimer <= 0) {
            this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.entity.bodyYaw, 5.0F);  // 原版是10.0F
        }
    }
}
