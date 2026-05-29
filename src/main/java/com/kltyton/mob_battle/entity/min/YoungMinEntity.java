package com.kltyton.mob_battle.entity.min;

import com.kltyton.mob_battle.entity.xunsheng.XunShengEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class YoungMinEntity extends Mob implements GeoEntity {

    public YoungMinEntity(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
        this.setNoAi(true); // 禁用默认AI
    }
    @Override
    public void knockback(double strength, double x, double z) {
    }
    @Override
    protected void blockedByItem(LivingEntity target) {
    }
    // 设置初始属性
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1500.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0); // 无法移动
    }

    @Override
    public void tick() {
        super.tick();

        // 仅在服务器端运行逻辑
        if (!this.level().isClientSide && this.tickCount % 20 == 0) { // 每秒执行一次，节省性能
            redirectWardenAnger();
            this.heal(10.0F);
        }
    }

    private void redirectWardenAnger() {
        // 1. 寻找 50 格内最近的实体（排除掉 YoungMin 自己）
        AABB targetSearchBox = this.getBoundingBox().inflate(50.0);
        List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(
                LivingEntity.class,
                targetSearchBox,
                entity -> entity != this && entity.isAlive() && !(entity instanceof Warden || entity instanceof YoungMinEntity || entity instanceof XunShengEntity)
        );

        // 找到最近的实体
        LivingEntity target = null;
        double closestDistance = Double.MAX_VALUE;
        Vec3 thisPos = this.position();

        for (LivingEntity entity : nearbyEntities) {
            double distance = thisPos.distanceTo(entity.position());
            if (distance < closestDistance) {
                closestDistance = distance;
                target = entity;
            }
        }

        if (target == null) return;

        // 2. 寻找 64 格内的监守者
        AABB wardenSearchBox = this.getBoundingBox().inflate(64.0);
        List<Warden> wardens = this.level().getEntitiesOfClass(
                Warden.class,
                wardenSearchBox,
                EntitySelector.NO_SPECTATORS
        );
        List<XunShengEntity> xunShengEntities = this.level().getEntitiesOfClass(
                XunShengEntity.class,
                wardenSearchBox,
                EntitySelector.NO_SPECTATORS
        );

        // 3. 强制让监守者愤怒并攻击该目标
        for (Warden warden : wardens) {
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
    public void push(Entity entity) {
    }

    @Override
    public boolean canCollideWith(Entity other) {
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
