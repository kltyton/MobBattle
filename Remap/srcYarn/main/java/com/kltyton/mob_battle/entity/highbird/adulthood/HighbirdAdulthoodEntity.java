package com.kltyton.mob_battle.entity.highbird.adulthood;

import com.kltyton.mob_battle.entity.highbird.HighbirdBaseEntity;
import com.kltyton.mob_battle.entity.highbird.goals.*;
import com.kltyton.mob_battle.entity.highbird.predicate.NonHighbirdPredicate;
import com.kltyton.mob_battle.network.packet.HighbirdAttackPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;;

public class HighbirdAdulthoodEntity extends HighbirdBaseEntity {
    // 在类顶部添加新字段
    private static final double SLEEP_RANGE = 3.0; // 在巢穴3格内睡觉
    private Goal moveToNestGoal; // 移动到巢穴的目标
    private BlockPos nestPos; // 存储巢穴位置
    private static final double NEST_RADIUS = 15.0; // 巢穴游荡范围
    public static final int HAY_BLOCK_CHECK_INTERVAL = 200; // 检查干草块的间隔（刻）
    protected static final RawAnimation ANGER_ANIM = RawAnimation.begin().thenPlay("yujing");
    public boolean angerTriggered = false;

    public HighbirdAdulthoodEntity(EntityType<? extends HighbirdAdulthoodEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;
        this.getNavigation().setCanSwim(true);
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
        if (!this.getWorld().isClient) {
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
                    if (!this.goalSelector.getGoals().contains(moveToNestGoal)) {
                        this.goalSelector.add(3, moveToNestGoal);
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
                this.setVelocity(0, this.getVelocity().y, 0);
                this.velocityDirty = true;
            }
        }
    }
    @Override
    public void startSleeping() {
        if (!isSleeping) {
            isSleeping = true;
            this.triggerAnim("sleep_controller", "sleep");
            this.getNavigation().stop();
            this.setAiDisabled(true);

            // 移除移动到巢穴的目标
            this.goalSelector.remove(moveToNestGoal);
        }
    }
    @Override
    public boolean canSee(Entity entity, RaycastContext.ShapeType shapeType, RaycastContext.FluidHandling fluidHandling, double entityY) {
        return true;
    }
    @Override
    protected void wake() {
        if (isSleeping) {
            this.isSleeping = false;
            this.triggerAnim("sleep_controller", "wake");
            this.setAiDisabled(false);

            // 移除移动到巢穴的目标
            this.goalSelector.remove(moveToNestGoal);
        }
    }
    // 新增方法：检查是否在巢穴附近
    public boolean isNearNest() {
        if (!hasNest()) return false;
        BlockPos nest = getNestPos();
        double distance = Math.sqrt(squaredDistanceTo(nest.getX(), nest.getY(), nest.getZ()));
        return distance <= SLEEP_RANGE;
    }
    @Override
    protected boolean startGrowth() {
        return false;
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));

        // ★ 1. 先播放愤怒动画（优先级高于攻击）
        this.goalSelector.add(1, new AngerAnimationGoal(this));

        // ★ 2. 原来的攻击 Goal 放后面，动画播完后才会轮到它
        this.goalSelector.add(2, new HABMeleeAttackGoal(this, 1.2, false));

        // 其余保持不变...
        this.goalSelector.add(3, new FindNestGoal(this));
        this.goalSelector.add(4, new SmartWanderGoal(this, 0.8, NEST_RADIUS, 0.001));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new HABActiveTargetGoal<>(this, LivingEntity.class,
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
                getWorld().getBlockState(nestPos).getBlock() == Blocks.HAY_BLOCK;
    }

    public static DefaultAttributeContainer.Builder createHighbirdAttributes() {
        return AnimalEntity.createAnimalAttributes()
                .add(EntityAttributes.MAX_HEALTH, 800.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 50.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.6F)
                .add(EntityAttributes.FOLLOW_RANGE, 24.0D); // 索敌距离
    }
    @Override
    protected Box getAttackBox() {
        Box box = super.getAttackBox();
        return box.contract(3.0, 0.0, 3.0);
    }
}
