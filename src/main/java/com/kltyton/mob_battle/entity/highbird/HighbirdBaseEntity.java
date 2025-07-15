package com.kltyton.mob_battle.entity.highbird;

import com.kltyton.mob_battle.entity.highbird.goals.HighbirdMeleeAttackGoal;
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

public abstract class HighbirdBaseEntity extends TameableEntity implements GeoEntity {

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
    private int stateTransitionTimer = 0; // 状态过渡计时器
    private int attackCooldown = 0;
    private boolean isSleepTransitioning = false; // 是否正在睡眠过渡中

    // 动画时长（ticks）
    private static final int SLEEP_ANIM_DURATION = 20; // 假设sleep动画20ticks
    private static final int WAKE_ANIM_DURATION = 20;  // 假设wake动画20ticks

    public HighbirdBaseEntity(EntityType<? extends HighbirdBaseEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new HighbirdMeleeAttackGoal(this, 1.2, false));
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
            // 处理状态过渡计时器
            if (isSleepTransitioning) {
                stateTransitionTimer++;
            }

            // 处理状态过渡完成
            if (isSleepTransitioning) {
                if (currentState == HighbirdState.SLEEP && stateTransitionTimer >= SLEEP_ANIM_DURATION) {
                    // 睡眠动画完成，进入睡眠状态
                    setState(HighbirdState.SLEEPING);
                    isSleepTransitioning = false;
                } else if (currentState == HighbirdState.WAKE && stateTransitionTimer >= WAKE_ANIM_DURATION) {
                    // 醒来动画完成，进入空闲状态
                    setState(HighbirdState.IDLE);
                    isSleepTransitioning = false;
                }
            }

            // 检查是否应该睡觉（夜晚+在地面上）
            if (shouldSleep() && currentState != HighbirdState.SLEEPING &&
                    currentState != HighbirdState.SLEEP && currentState != HighbirdState.WAKE) {
                startSleeping();
            }

            // 检查是否应该醒来（白天或受伤）
            if (shouldWakeUp() && currentState == HighbirdState.SLEEPING) {
                wakeUp();
            }

            // 攻击冷却
            if (attackCooldown > 0) {
                attackCooldown--;
            }

            // 在非睡眠状态更新移动状态
            if (!isSleeping() && !isSleepTransitioning && attackCooldown <= 0 && !this.isDead()) {
                if (this.isMoving()) {
                    setState(HighbirdState.WALK);
                } else {
                    setState(HighbirdState.IDLE);
                }
            }

            // 睡眠时禁止移动
            if (isSleeping()) {
                this.getNavigation().stop();
                this.setVelocity(0, this.getVelocity().y, 0);
                this.velocityDirty = true;
            }
        }
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        // 如果正在睡觉，先醒来再攻击
        if (isSleeping()) {
            wakeUp();
        }

        if (super.tryAttack(world, target)) {
            // 触发攻击动画
            this.triggerAnim("attack_controller", "attack");
            attackCooldown = 40; // 1秒冷却
            setState(HighbirdState.ATTACK);
            return true;
        }
        return false;
    }

    /* ======================
       睡眠系统（重写版）
       ====================== */
    // 是否正在睡眠（包括过渡状态）
    public boolean isSleeping() {
        return currentState == HighbirdState.SLEEPING ||
                currentState == HighbirdState.SLEEP ||
                currentState == HighbirdState.WAKE;
    }

    // 检查睡觉条件
    private boolean shouldSleep() {
        return !this.getWorld().isDay() &&
                this.isOnGround() &&
                !this.isAttacking() &&
                attackCooldown <= 0;
    }

    // 检查醒来条件
    private boolean shouldWakeUp() {
        return this.getWorld().isDay() ||
                this.hurtTime > 0;
    }

    // 开始睡眠
    public void startSleeping() {
        if (!isSleeping()) {
            setState(HighbirdState.SLEEP);
            isSleepTransitioning = true;
            stateTransitionTimer = 0;
            this.getNavigation().stop();
        }
    }

    // 唤醒实体
    public void wakeUp() {
        if (currentState == HighbirdState.SLEEPING) {
            setState(HighbirdState.WAKE);
            isSleepTransitioning = true;
            stateTransitionTimer = 0;
        }
    }

    // 是否正在攻击
    public boolean isAttacking() {
        return currentState == HighbirdState.ATTACK;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        // 睡眠时被右键点击则醒来
        if (currentState == HighbirdState.SLEEPING) {
            wakeUp();
            return ActionResult.SUCCESS;
        }

        // 使用床物品强制睡眠
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() == Items.WHITE_BED && !isSleeping()) {
            startSleeping();
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    /* ======================
       状态管理
       ====================== */
    public void setState(HighbirdState state) {
        // 只有在状态改变时更新
        if (this.currentState != state) {
            this.currentState = state;
        }
    }

    /* ======================
       GeckoLib 动画控制器
       ====================== */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 主控制器：负责所有常规状态
        controllers.add(new AnimationController<>("main_controller", 5, this::mainController));
        // 攻击控制器
        controllers.add(
                new AnimationController<>("attack_controller", state -> PlayState.STOP)
                        .triggerableAnim("attack", ATTACK_ANIM)
        );
    }

    private PlayState mainController(final AnimationTest<HighbirdBaseEntity> event) {
        // 优先处理特殊状态
        switch (currentState) {
            case SLEEP:
                return event.setAndContinue(SLEEP_ANIM);
            case SLEEPING:
                return event.setAndContinue(SLEEPING_ANIM);
            case WAKE:
                return event.setAndContinue(WAKE_ANIM);
            case DEATH:
                return event.setAndContinue(DEATH_ANIM);
            case ATTACK:
                // 攻击动画由独立控制器处理
                return PlayState.STOP;
        }

        // 处理常规状态
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
        return false;
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
}