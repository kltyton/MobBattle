package com.kltyton.mob_battle.entity.drone.goal;

import java.util.EnumSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.phys.Vec3;

public class FlyRangedAttackGoal extends Goal {
    private final RangedAttackMob owner;
    private final double mobSpeed;
    private final float attackRadius;      // 理想攻击距离
    private final float maxAttackDistance; // 最大射程
    private final int minAttackInterval;
    private final int maxAttackInterval;

    private LivingEntity target;
    private int combatTicks = 0;
    private int attackCooldown = 0;

    public FlyRangedAttackGoal(RangedAttackMob owner, double speed, int minInterval, int maxInterval, float attackRadius) {
        this.owner = owner;
        this.mobSpeed = speed;
        this.minAttackInterval = minInterval;
        this.maxAttackInterval = maxInterval;
        this.attackRadius = attackRadius;
        this.maxAttackDistance = attackRadius + 8.0F; // 稍微宽容点
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity potential = ((Mob)owner).getTarget();
        if (potential != null && potential.isAlive()) {
            this.target = potential;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() || this.target.isAlive();
    }

    @Override
    public void stop() {
        this.target = null;
        this.combatTicks = 0;
        this.attackCooldown = 0;
    }

    @Override
    public void tick() {
        if (!(owner instanceof Mob mob)) return;
        if (target == null || !target.isAlive()) {
            this.target = mob.getTarget();
            if (target == null) return;
        }

        double distanceSq = mob.distanceToSqr(target);
        double idealDistSq = attackRadius * attackRadius;
        double maxDistSq = maxAttackDistance * maxAttackDistance;
        boolean canSee = mob.getSensing().hasLineOfSight(target);

        // ============ 1. 移动控制 - 关键改动 ============
        // 太远：直接追
        if (distanceSq > maxDistSq) {
            mob.getNavigation().moveTo(
                    target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), mobSpeed);
        }
        // 太近：只在需要的时候才后退，并且保持同一个方向一段时间
        else if (distanceSq < idealDistSq * 0.64) { // 0.8^2，稍微宽松一点
            // 每 15~25 tick 才重新选择一次后退方向（关键！）
            if (this.combatTicks % 20 == 0 || mob.getNavigation().isDone()) {
                // 计算一个垂直于“自身→目标”方向的绕圈向量
                Vec3 toTarget = target.position().subtract(mob.position()).normalize();
                // 左右随机
                double side = mob.getRandom().nextBoolean() ? 1 : -1;
                // 旋转90度（2D平面）
                Vec3 circleDir = new Vec3(-toTarget.z * side, 0, toTarget.x * side).normalize();

                double retreatX = target.getX() + circleDir.x * 14;
                double retreatY = target.getY() + 6 + mob.getRandom().nextInt(5); // 高度随机一点
                double retreatZ = target.getZ() + circleDir.z * 14;

                mob.getNavigation().moveTo(retreatX, retreatY, retreatZ, mobSpeed);
            }
            // 已经有了路径，就继续飞，不要每 tick 都 stop/start
        } else {
            // 理想距离：只有在当前正在移动并且已经快到了，才停下来
            if (!mob.getNavigation().isDone()) {
                // 距离目标点小于 4 格时才真正停下来，防止提前刹车
                if (mob.getNavigation().getTargetPos().closerToCenterThan(mob.position(), 4.0)) {
                    mob.getNavigation().stop();
                }
            }
        }

        // ============ 2. 持续注视 ============
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        // ============ 3. 攻击 ============
        if (--attackCooldown <= 0 && canSee && distanceSq <= maxDistSq) {
            float f = (float) Math.sqrt(distanceSq) / this.attackRadius;
            float pullProgress = Mth.clamp(f, 0.1F, 1.0F);
            this.owner.performRangedAttack(this.target, pullProgress);
            attackCooldown = 15 + mob.getRandom().nextInt(10);
        }

        combatTicks++;
    }
}