package com.kltyton.mob_battle.entity.accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;

public class BigBossLookControl extends LookControl {
    public BigBossLookControl(Mob entity) {
        super(entity);
    }

    @Override
    public void setLookAt(Entity entity, float maxYawChange, float maxPitchChange) {
        super.setLookAt(entity, 30.0F, 30.0F);
    }

    @Override
    public void setLookAt(double x, double y, double z, float maxYawChange, float maxPitchChange) {
        super.setLookAt(x, y, z, 30.0F, 30.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.lookAtCooldown <= 0) {
            // 头部追随身体的速度
            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, 10.0F);
        }
    }
}
