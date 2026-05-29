package com.kltyton.mob_battle.entity.highbird.adulthood;

import com.kltyton.mob_battle.entity.highbird.HighbirdBaseEntity;
import com.kltyton.mob_battle.entity.highbird.goals.*;
import com.kltyton.mob_battle.entity.highbird.predicate.NonHighbirdPredicate;
import com.kltyton.mob_battle.network.packet.HighbirdAttackPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class HighbirdAdulthoodEntity extends HighbirdBaseEntity {
    // 在类顶部添加新字段
    private static final double SLEEP_RANGE = 3.0; // 在巢穴3格内睡觉
    private Goal moveToNestGoal; // 移动到巢穴的目标
    private BlockPos nestPos; // 存储巢穴位置
    private static final double NEST_RADIUS = 15.0; // 巢穴游荡范围
    public static final int HAY_BLOCK_CHECK_INTERVAL = 200; // 检查干草块的间隔（刻）
    protected static final RawAnimation ANGER_ANIM = RawAnimation.begin().thenPlay("yujing");
    public boolean angerTriggered = false;

    public HighbirdAdulthoodEntity(EntityType<? extends HighbirdAdulthoodEntity> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 5;
        this.getNavigation().setCanFloat(true);
        this.moveToNestGoal = new MoveToNestForSleepGoal(this);
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        // 攻击控制器
        controllers.add(
                new AnimationController<>("anger_controller", state -> PlayState.STOP)
                        .triggerableAnim("anger", ANGER_ANIM));
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (getTarget() == null || !getTarget().isAlive()) {
                angerTriggered = false;
            }
            if (forcedWakeUp) {
                if (excitedTime > 0) {
                    excitedTime--;
                } else {
                    forcedWakeUp = false;
                    excitedTime = 1200;
                }
            }
            // 修改睡觉逻辑
            if (shouldSleep() && !isSleeping && !forcedWakeUp) {
                // 如果有巢穴且不在巢穴附近，先移动到巢穴
                if (hasNest() && !isNearNest()) {
                    // 添加移动到巢穴目标
                    if (!this.goalSelector.getAvailableGoals().contains(moveToNestGoal)) {
                        this.goalSelector.addGoal(3, moveToNestGoal);
                    }
                } else {
                    // 在巢穴附近或没有巢穴，直接睡觉
                    startSleeping();
                }
            }
            if (shouldWakeUp() && isSleeping) {
                wake();
            }
            // 如果正在睡觉且不在巢穴附近，醒来
            if (isSleeping && hasNest() && !isNearNest()) {
                wake();
            }

            // 睡眠时禁止移动
            if (isSleeping) {
                this.getNavigation().stop();
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
                this.hasImpulse = true;
            }
        }
    }
    @Override
    public void startSleeping() {
        if (!isSleeping) {
            isSleeping = true;
            this.triggerAnim("sleep_controller", "sleep");
            this.getNavigation().stop();
            this.setNoAi(true);

            // 移除移动到巢穴的目标
            this.goalSelector.removeGoal(moveToNestGoal);
        }
    }
    @Override
    public boolean hasLineOfSight(Entity entity, ClipContext.Block shapeType, ClipContext.Fluid fluidHandling, double entityY) {
        return true;
    }
    @Override
    protected void wake() {
        if (isSleeping) {
            this.isSleeping = false;
            this.triggerAnim("sleep_controller", "wake");
            this.setNoAi(false);

            // 移除移动到巢穴的目标
            this.goalSelector.removeGoal(moveToNestGoal);
        }
    }
    // 新增方法：检查是否在巢穴附近
    public boolean isNearNest() {
        if (!hasNest()) return false;
        BlockPos nest = getNestPos();
        double distance = Math.sqrt(distanceToSqr(nest.getX(), nest.getY(), nest.getZ()));
        return distance <= SLEEP_RANGE;
    }
    @Override
    protected boolean startGrowth() {
        return false;
    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // ★ 1. 先播放愤怒动画（优先级高于攻击）
        this.goalSelector.addGoal(1, new AngerAnimationGoal(this));

        // ★ 2. 原来的攻击 Goal 放后面，动画播完后才会轮到它
        this.goalSelector.addGoal(2, new HABMeleeAttackGoal(this, 1.2, false));

        // 其余保持不变...
        this.goalSelector.addGoal(3, new FindNestGoal(this));
        this.goalSelector.addGoal(4, new SmartWanderGoal(this, 0.8, NEST_RADIUS, 0.001));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new HABActiveTargetGoal<>(this, LivingEntity.class,
                10, false, false, new NonHighbirdPredicate()));
    }
    // ===== 巢穴相关方法 =====
    public void setNestPos(BlockPos pos) {
        this.nestPos = pos;
    }

    public BlockPos getNestPos() {
        return nestPos;
    }

    public boolean hasNest() {
        return nestPos != null;
    }

    // 检查巢穴是否有效（方块是否存在）
    public boolean isNestValid() {
        return hasNest() &&
                level().getBlockState(nestPos).getBlock() == Blocks.HAY_BLOCK;
    }

    public static AttributeSupplier.Builder createHighbirdAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 800.0D)
                .add(Attributes.ATTACK_DAMAGE, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.6F)
                .add(Attributes.FOLLOW_RANGE, 24.0D); // 索敌距离
    }
    @Override
    protected AABB getAttackBoundingBox() {
        AABB box = super.getAttackBoundingBox();
        return box.deflate(3.0, 0.0, 3.0);
    }
}
