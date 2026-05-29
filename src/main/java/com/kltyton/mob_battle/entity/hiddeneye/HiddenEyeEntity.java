package com.kltyton.mob_battle.entity.hiddeneye;

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
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HiddenEyeEntity extends Mob implements GeoEntity {

    public HiddenEyeEntity(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }

    // 设置初始属性
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0); // 无法移动
    }
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
    }
    @Override
    public void knockback(double strength, double x, double z) {
    }
    @Override
    protected void blockedByItem(LivingEntity target) {
    }

    @Override
    public void tick() {
        super.tick();
        // 仅在服务器端运行逻辑
        if (!this.level().isClientSide && this.tickCount % 20 == 0) { // 每秒执行一次，节省性能
            redirectWardenAnger();
        }
    }

    private void redirectWardenAnger() {
        // 寻找 300 格内的所有监守者和 XunShengEntity
        AABB searchBox = this.getBoundingBox().inflate(300.0);

        List<Warden> wardens = this.level().getEntitiesOfClass(
                Warden.class,
                searchBox,
                EntitySelector.NO_SPECTATORS.and(Entity::isAlive)
        );
        // 分别处理每只 Warden
        for (Warden warden : wardens) {
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
        double searchRange = mob.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB targetSearchBox = mob.getBoundingBox().inflate(searchRange);

        List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(
                LivingEntity.class,
                targetSearchBox,
                entity -> // 排除 HiddenEye 自己
                        entity.isAlive()
                                && entity != mob
                                && !(entity instanceof Warden)
                                && !(entity instanceof HiddenEyeEntity)
        );

        LivingEntity closest = null;
        double closestDistance = Double.MAX_VALUE;
        Vec3 mobPos = mob.position();

        for (LivingEntity candidate : nearbyEntities) {
            double dist = mobPos.distanceToSqr(candidate.position());
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

