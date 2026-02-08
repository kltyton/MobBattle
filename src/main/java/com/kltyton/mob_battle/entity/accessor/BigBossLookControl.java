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
        super.lookAt(entity, 30.0F, 30.0F);
    }

    @Override
    public void lookAt(double x, double y, double z, float maxYawChange, float maxPitchChange) {
        super.lookAt(x, y, z, 30.0F, 30.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.lookAtTimer <= 0) {
            // 头部追随身体的速度
            this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.entity.bodyYaw, 10.0F);
        }
    }
}
