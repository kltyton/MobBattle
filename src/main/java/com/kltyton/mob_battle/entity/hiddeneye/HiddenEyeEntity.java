package com.kltyton.mob_battle.entity.hiddeneye;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class HiddenEyeEntity extends MobEntity implements GeoEntity {

    public HiddenEyeEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    // 设置初始属性
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 200.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.0); // 无法移动
    }
    protected void initGoals() {
        this.goalSelector.add(0, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(1, new LookAroundGoal(this));
    }
    @Override
    public void takeKnockback(double strength, double x, double z) {
    }
    @Override
    protected void knockback(LivingEntity target) {
    }

    @Override
    public void tick() {
        super.tick();
        // 仅在服务器端运行逻辑
        if (!this.getWorld().isClient && this.age % 20 == 0) { // 每秒执行一次，节省性能
            redirectWardenAnger();
        }
    }

    private void redirectWardenAnger() {
        // 寻找 300 格内的所有监守者和 XunShengEntity
        Box searchBox = this.getBoundingBox().expand(300.0);

        List<WardenEntity> wardens = this.getWorld().getEntitiesByClass(
                WardenEntity.class,
                searchBox,
                EntityPredicates.EXCEPT_SPECTATOR.and(Entity::isAlive)
        );
        // 分别处理每只 Warden
        for (WardenEntity warden : wardens) {
            LivingEntity nearestTarget = findNearestValidTargetFor(warden);
            if (nearestTarget != null) {
                // Warden 使用特殊的愤怒系统，150 是最高愤怒值，true 表示立即触发吼叫等行为
                warden.increaseAngerAt(nearestTarget, 150, true);
                warden.setTarget(nearestTarget);
            }
        }
    }

    private LivingEntity findNearestValidTargetFor(LivingEntity mob) {
        // Warden 的默认感知/跟踪范围大约是 24~32 格，这里用 40 格作为安全值
        double searchRange = mob.getAttributeValue(EntityAttributes.FOLLOW_RANGE);
        Box targetSearchBox = mob.getBoundingBox().expand(searchRange);

        List<LivingEntity> nearbyEntities = this.getWorld().getEntitiesByClass(
                LivingEntity.class,
                targetSearchBox,
                entity -> // 排除 HiddenEye 自己
                        entity.isAlive()
                                && entity != mob
                                && !(entity instanceof WardenEntity)
                                && !(entity instanceof HiddenEyeEntity)
        );

        LivingEntity closest = null;
        double closestDistance = Double.MAX_VALUE;
        Vec3d mobPos = mob.getPos();

        for (LivingEntity candidate : nearbyEntities) {
            double dist = mobPos.squaredDistanceTo(candidate.getPos());
            if (dist < closestDistance) {
                closestDistance = dist;
                closest = candidate;
            }
        }

        return closest;
    }

    // --- 碰撞与挤压处理 ---

    @Override
    public boolean isPushable() {
        return false; // 不会被其他实体挤走
    }

    @Override
    public void pushAwayFrom(Entity entity) {
    }

    @Override
    public boolean collidesWith(Entity other) {
        return false; // 没有碰撞体积
    }
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", animTest -> animTest.setAndContinue(IDEA_ANIM)));
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

