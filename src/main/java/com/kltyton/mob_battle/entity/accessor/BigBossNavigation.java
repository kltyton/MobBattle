package com.kltyton.mob_battle.entity.accessor;

import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BigBossNavigation extends MobNavigation {
    public BigBossNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    public void tick() {
        if (this.currentPath != null && !this.currentPath.isFinished()) {
            this.nodeReachProximity = Math.max(1.5F, this.entity.getWidth() * 0.8F);
        }
        super.tick();
    }

    @Override
    protected void continueFollowingPath() {
        if (this.currentPath == null || this.currentPath.isFinished()) {
            return;
        }

        Vec3d entityPos = this.getPos();
        Vec3d targetNodePos = this.currentPath.getNodePosition(this.entity);

        double distanceSq = entityPos.squaredDistanceTo(targetNodePos);
        double reachSq = this.nodeReachProximity * this.nodeReachProximity;

        if (distanceSq < reachSq) {
            // 只有当还有下一个节点时才推进
            if (this.currentPath.getCurrentNodeIndex() < this.currentPath.getLength() - 1) {
                this.currentPath.next();
            } else {
                this.stop();
                return;
            }
        }
        if (this.currentPath != null && !this.currentPath.isFinished()) {
            super.continueFollowingPath();
        }
    }

    @Override
    protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target) {
        return doesNotCollide(this.entity, origin, target, true);
    }
}
