package com.kltyton.mob_battle.entity.highbird;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HighbirdBase extends TameableEntity implements GeoEntity {

    /* ---------- 动画定义 ---------- */
    protected static final RawAnimation IDLE_ANIM   = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM   = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation DEATH_ANIM  = RawAnimation.begin().thenPlayAndHold("death");
    protected static final RawAnimation SLEEP_ANIM  = RawAnimation.begin().thenPlay("sleep");
    protected static final RawAnimation WAKE_ANIM   = RawAnimation.begin().thenPlay("wake");
    protected static final RawAnimation SLEEPING_ANIM = RawAnimation.begin().thenLoop("sleeping");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    /* ---------- 状态枚举 ---------- */
    public enum HighbirdState {
        IDLE, WALK, ATTACK, DEATH, SLEEP, WAKE, SLEEPING
    }

    // 当前状态字段
    private HighbirdState currentState = HighbirdState.IDLE;

    private boolean isSleeping = false;
    private int sleepTimer = 0;
    private int attackCooldown = 0;

    public HighbirdBase(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }
/*
    // 注册实体属性（生命值、移动速度等）
    public static DefaultAttributeContainer.Builder createHighbirdAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
    }*/

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));
    }

    public boolean isMoving() {
        return this.getVelocity().horizontalLengthSquared() > 0.001;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (!isSleeping && !this.getWorld().isDay() && this.isOnGround() && this.random.nextFloat() < 0.005) {
                startSleeping();
            }
            // 更新睡觉状态
            if (isSleeping) {
                sleepTimer++;
                // 检查是否应该醒来
                if (shouldWakeUp()) {
                    wakeUp();
                }
                // 播放睡眠动画
                if (currentState != HighbirdState.SLEEPING) {
                    setState(HighbirdState.SLEEPING);
                }
            } else {
                sleepTimer = 0;
            }

            // 攻击冷却
            if (attackCooldown > 0) {
                attackCooldown--;
            }
            // 自动状态更新（当不处于特殊状态时）
            if (attackCooldown <= 0 && !isSleeping && !this.isDead()) {
                if (this.isMoving()) {
                    setState(HighbirdState.WALK);
                } else {
                    setState(HighbirdState.IDLE);
                }
            }
        }
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        if (super.tryAttack(world,target)) {
            // 触发攻击动画
            this.triggerAnim("attack_controller", "attack");
            attackCooldown = 40; // 1秒冷却
            setState(HighbirdState.ATTACK);
            return true;
        }
        return false;
    }

    /* ---------- 睡觉系统 ---------- */
    public void startSleeping() {
        if (!isSleeping) {
            isSleeping = true;
            setState(HighbirdState.SLEEP);
            this.getNavigation().stop();
        }
    }

    public void wakeUp() {
        if (isSleeping) {
            isSleeping = false;
            setState(HighbirdState.WAKE);
            // 设置计时器，在WAKE动画结束后回到IDLE
            this.sleepTimer = -20; // 20 tick后切换状态
        }
    }

    private boolean shouldWakeUp() {
        // 被伤害时醒来
        if (this.hurtTime > 0) return true;

        // 玩家右键点击时醒来
        // 实际交互在interactMob方法中处理

        // 随机醒来（示例：每100tick有1%几率醒来）
        //return sleepTimer > 100 && this.random.nextFloat() < 0.01;
        // 白天自动醒来
        return this.getWorld().isDay();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (isSleeping) {
            wakeUp();
            return ActionResult.SUCCESS;
        }

        // 检查是否使用睡觉物品（例如床）
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() == Items.WHITE_BED) {
            startSleeping();
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    /* ======================
       对外提供的状态切换接口
       ====================== */
    public void setState(HighbirdState state) {
        this.currentState = state;
    }

    /* ======================
       GeckoLib 动画控制器
       ====================== */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 主控制器：负责所有常规状态
        controllers.add(new AnimationController<>("main_controller", 5, this::mainController));
        // 攻击控制器：保持 triggerableAnim 的写法
        controllers.add(
                new AnimationController<>("attack_controller", state -> PlayState.STOP)
                        .triggerableAnim("attack", ATTACK_ANIM)
        );
    }

    private PlayState mainController(final AnimationTest<HighbirdBase> event) {
        // 处理一次性动画的过渡
        switch (currentState) {
            case SLEEP:
                if (event.controller().getAnimationState() == AnimationController.State.STOPPED) {
                    return event.setAndContinue(SLEEP_ANIM);
                }
            case WAKE:
                // WAKE动画结束后自动回到IDLE
                if (event.controller().getAnimationState() == AnimationController.State.STOPPED) {
                    return event.setAndContinue(WAKE_ANIM);
                }
                if (event.controller().hasAnimationFinished()) {
                    setState(HighbirdState.IDLE);
                }
            case DEATH:
                return event.setAndContinue(DEATH_ANIM);
        }

        // 循环动画处理
        if (isSleeping) {
            return event.setAndContinue(SLEEPING_ANIM);
        }

        if (this.isDead()) {
            return event.setAndContinue(DEATH_ANIM);
        }
        if (event.isMoving()) {
            return event.setAndContinue(WALK_ANIM);
        } else {
            return event.setAndContinue(IDLE_ANIM);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    /* ---------- TameableEntity 必须实现的抽象方法 ---------- */
    @Override
    public boolean isBreedingItem(ItemStack stack) {
        /*return stack.getItem() == net.minecraft.item.Items.WHEAT; // 示例*/
        return false;
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null; // 暂时不实现繁殖
    }
}