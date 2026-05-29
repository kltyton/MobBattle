package com.kltyton.mob_battle.entity.accessor;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BigBossNavigation extends GroundPathNavigation {
    public BigBossNavigation(Mob mobEntity, Level world) {
        super(mobEntity, world);
    }

    @Override
    public void tick() {
        if (this.path != null && !this.path.isDone()) {
            this.maxDistanceToWaypoint = Math.max(1.5F, this.mob.getBbWidth() * 0.9F);
        }
        super.tick();
    }
    @Override
    protected void followThePath() {
        if (this.path == null || this.path.isDone()) {
            return;
        }

        Vec3 entityPos = this.getTempMobPos();
        Vec3 targetNodePos = this.path.getNextEntityPos(this.mob);

        double distanceSq = entityPos.distanceToSqr(targetNodePos);
        double reachSq = this.maxDistanceToWaypoint * this.maxDistanceToWaypoint;

        if (distanceSq < reachSq) {
            // 只有当还有下一个节点时才推进
            if (this.path.getNextNodeIndex() < this.path.getNodeCount() - 1) {
                this.path.advance();
            } else {
                this.stop();
                return;
            }
        }
        if (this.path != null && !this.path.isDone()) {
            super.followThePath();
        }
    }

    @Override
    protected boolean canMoveDirectly(Vec3 origin, Vec3 target) {
        return isClearForMovementBetween(this.mob, origin, target, true);
    }
}
