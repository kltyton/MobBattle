package com.kltyton.mob_battle.entity.sugarmanscorpion;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

public class SuicideHealGoal extends Goal {
    private final Mob host;
    private LivingEntity target;
    private final double speed;
    private final double searchRadius;

    public SuicideHealGoal(SugarManScorpion host, double speed, double searchRadius) {
        this.host = host;
        this.speed = speed;
        this.searchRadius = searchRadius;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // 寻找满足条件的最小距离实体
        AABB searchBox = this.host.getBoundingBox().inflate(this.searchRadius);
        List<LivingEntity> list = this.host.level().getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != this.host
                        && entity.getTags().contains("yuanxin")
                        && this.host.isAlliedTo(entity)
                        && entity.getHealth() < 200.0F
        );

        if (list.isEmpty()) {
            this.target = null;
            return false;
        }

        // 选取最近的
        this.target = list.stream()
                .min(Comparator.comparingDouble(this.host::distanceToSqr))
                .orElse(null);

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.target == null) return false;
        if (!this.target.isAlive()) return false;
        // 若目标被治愈到 >=200 或者生命值>=最大也可提前终止（可按需调整）
        if (this.target.getHealth() >= 200.0F) return false;
        // 若目标超出搜索半径 + 5（防止被拉太远）则停止
        double maxDist = this.searchRadius + 5.0;
        return !(this.host.distanceToSqr(this.target) > maxDist * maxDist);
    }

    @Override
    public void start() {
        if (this.target != null) {
            // 发起路径寻路（会持续在 tick() 中重新发起，保证持续跟随）
            this.host.getNavigation().moveTo(this.target, this.speed);
        }
    }

    @Override
    public void stop() {
        this.target = null;
        this.host.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.target == null) return;

        // 如果导航空闲或目标移动了，继续发起移动命令
        if (this.host.getNavigation().isDone()) {
            this.host.getNavigation().moveTo(this.target, this.speed);
        }

        // 到达检测（这里用距离平方 <= 4 表示靠近 2 方块以内）
        double reachSq = 4.0;
        if (this.host.distanceToSqr(this.target) <= reachSq) {
            if (this.target instanceof LivingEntity livingTarget) {
                // 计算实际恢复量：不超过最大生命
                float maxHP = livingTarget.getMaxHealth();
                float missing = maxHP - livingTarget.getHealth();
                float healAmount = Math.min(170.0F, missing);
                if (healAmount > 0.0F) {
                    livingTarget.heal(healAmount);
                }
            }

            // 自杀：用魔法伤害杀死自己（会走普通死亡流程）
            if (!this.host.level().isClientSide) this.host.kill((ServerLevel) this.host.level());
            this.host.discard();
            // 停止目标（已死亡或移除）
            this.stop();
        }
    }
}

