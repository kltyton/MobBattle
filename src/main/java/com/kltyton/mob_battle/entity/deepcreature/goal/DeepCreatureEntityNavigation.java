package com.kltyton.mob_battle.entity.deepcreature.goal;

import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class DeepCreatureEntityNavigation extends MobNavigation {
    public DeepCreatureEntityNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }
/*    @Override
    protected void continueFollowingPath() {
        this.nodeReachProximity = this.entity.getWidth() * 0.8F;
        super.continueFollowingPath();
    }*/
}
