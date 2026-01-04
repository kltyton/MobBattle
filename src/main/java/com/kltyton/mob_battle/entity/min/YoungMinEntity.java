package com.kltyton.mob_battle.entity.min;

import com.kltyton.mob_battle.entity.xunsheng.XunShengEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WardenEntity;
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

public class YoungMinEntity extends MobEntity implements GeoEntity {

    public YoungMinEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        this.setAiDisabled(true); // 禁用默认AI
    }
    @Override
    public void takeKnockback(double strength, double x, double z) {
    }
    @Override
    protected void knockback(LivingEntity target) {
    }
    // 设置初始属性
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 1500.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.0); // 无法移动
    }

    @Override
    public void tick() {
        super.tick();

        // 仅在服务器端运行逻辑
        if (!this.getWorld().isClient && this.age % 20 == 0) { // 每秒执行一次，节省性能
            redirectWardenAnger();
            this.heal(10.0F);
        }
    }

    private void redirectWardenAnger() {
        // 1. 寻找 50 格内最近的实体（排除掉 YoungMin 自己）
        Box targetSearchBox = this.getBoundingBox().expand(50.0);
        List<LivingEntity> nearbyEntities = this.getWorld().getEntitiesByClass(
                LivingEntity.class,
                targetSearchBox,
                entity -> entity != this && entity.isAlive() && !(entity instanceof WardenEntity || entity instanceof YoungMinEntity || entity instanceof XunShengEntity)
        );

        // 找到最近的实体
        LivingEntity target = null;
        double closestDistance = Double.MAX_VALUE;
        Vec3d thisPos = this.getPos();

        for (LivingEntity entity : nearbyEntities) {
            double distance = thisPos.distanceTo(entity.getPos());
            if (distance < closestDistance) {
                closestDistance = distance;
                target = entity;
            }
        }

        if (target == null) return;

        // 2. 寻找 64 格内的监守者
        Box wardenSearchBox = this.getBoundingBox().expand(64.0);
        List<WardenEntity> wardens = this.getWorld().getEntitiesByClass(
                WardenEntity.class,
                wardenSearchBox,
                EntityPredicates.EXCEPT_SPECTATOR
        );
        List<XunShengEntity> xunShengEntities = this.getWorld().getEntitiesByClass(
                XunShengEntity.class,
                wardenSearchBox,
                EntityPredicates.EXCEPT_SPECTATOR
        );

        // 3. 强制让监守者愤怒并攻击该目标
        for (WardenEntity warden : wardens) {
            // 监守者使用特殊的愤怒系统，直接设置最高愤怒值
            warden.increaseAngerAt(target, 150, true);
            warden.setTarget(target);
        }
        for (XunShengEntity xunSheng : xunShengEntities) {
            xunSheng.setTarget(target);
        }
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
