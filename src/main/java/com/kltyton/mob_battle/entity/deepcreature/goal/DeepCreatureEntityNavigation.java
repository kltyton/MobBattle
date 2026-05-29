package com.kltyton.mob_battle.entity.deepcreature.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;

public class DeepCreatureEntityNavigation extends GroundPathNavigation {
    public DeepCreatureEntityNavigation(Mob mobEntity, Level world) {
        super(mobEntity, world);
    }
/*    @Override
    protected void continueFollowingPath() {
        this.nodeReachProximity = this.entity.getWidth() * 0.8F;
        super.continueFollowingPath();
    }*/
}
