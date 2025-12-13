package com.kltyton.mob_battle.entity.xunsheng;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public class XunShengEntity extends HostileEntity implements GeoEntity {
    private static final TrackedData<Integer> XUN_SHENG_TYPE = DataTracker.registerData(XunShengEntity.class, TrackedDataHandlerRegistry.INTEGER);
    // 新增：攻击冷却跟踪数据
    private static final TrackedData<Boolean> ATTACK_COOLDOWN = DataTracker.registerData(XunShengEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    // 新增：固定能力冷却跟踪数据
    private static final TrackedData<Boolean> FIXED_ABILITY_COOLDOWN = DataTracker.registerData(XunShengEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> FIXED_COOLDOWN_TICKS = DataTracker.registerData(XunShengEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private int cooldownTicks = 0; // 冷却计时器（仅服务端使用）
    // 新增：检查是否处于攻击冷却
    public boolean isAttackCooldown() {
        return this.getDataTracker().get(ATTACK_COOLDOWN);
    }
    // 构造函数，接收实体类型和等级作为参数，用于实体初始化
    public XunShengEntity(EntityType<? extends XunShengEntity> type, World level) {
        super(type, level); // 调用父类 PathfinderMob 的构造函数进行初始化
        this.experiencePoints = 5;
        this.getNavigation().setCanSwim(true);
    }
    // 新增：检查固定能力是否冷却
    public boolean isFixedAbilityOnCooldown() {
        return this.getDataTracker().get(FIXED_ABILITY_COOLDOWN);
    }
    /*


     动画定义

     */
    // 使用 RawAnimation.begin() 开始动画定义，然后通过 thenLoop("idea") 指定动画资源名称并设置循环播放
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    // 创建一个 AnimatableInstanceCache 对象，用于存储动画实例缓存
    // AnimatableInstanceCache 用于管理实体的动画控制器和动画状态
    // GeckoLibUtil.createInstanceCache(this) 创建缓存实例并将其与当前实体关联
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    // 重写 registerControllers 方法，用于注册动画控制器
    // 该方法将动画控制器添加到实体的动画管理系统中
    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {

        // 添加一个名为 "Flying" 的动画控制器
        // 动画控制器负责决定何时播放特定的动画
        controllers.add(new AnimationController<>("main_controller", 5 ,this::animationController));
        controllers.add(new AnimationController<>( "attack_controller",animTest -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK_ANIM));
        // 参数解释：
        // "Flying"：控制器名称
        // 5：控制器的优先级
        // this::flyAnimController：引用当前类中的 flyAnimController 方法作为动画控制逻辑
    }
    // 定义动画控制逻辑方法 flyAnimController
    // 该方法根据实体状态决定是否播放飞行动画
    // 泛型参数 <E extends ExampleEntity> 表示该方法可以处理 ExampleEntity 及其子类的实例
    private PlayState animationController(final AnimationTest<XunShengEntity> state) {
        if (state.isMoving()) {
            // 移动状态时播放行走动画
            return state.setAndContinue(WALK_ANIM);
        } else {
            // 空闲状态时播放待机动画
            return state.setAndContinue(IDEA_ANIM);
        }
    }
    // 重写 getAnimatableInstanceCache 方法，返回当前实体的动画实例缓存
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache; // 返回之前创建的 geoCache 缓存实例
    }




    /*

    目标选择区块

    */
    // 初始化目标选择器
    @Override
    protected void initGoals() {
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F)); // 添加看向玩家的目标
        this.goalSelector.add(8, new LookAroundGoal(this)); // 添加环顾四周的目标
        this.initCustomGoals(); // 初始化自定义目标
    }
    // 初始化自定义目标
    protected void initCustomGoals() {
        this.goalSelector.add(2, new XunShengAttackGoal(this, 1.0, false)); // 添加僵尸攻击目标
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0)); // 添加远距离游荡目标
        this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge(ZombifiedPiglinEntity.class)); // 添加复仇目标
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true)); // 添加主动攻击玩家目标
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false)); // 添加攻击村民目标
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true)); // 添加攻击铁傀儡目标
    }
    // 初始化数据追踪器
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(XUN_SHENG_TYPE, 0); // 添加僵尸类型追踪数据
        builder.add(ATTACK_COOLDOWN, false); // 初始化为非冷却状态

        // 新增固定能力冷却跟踪
        builder.add(FIXED_ABILITY_COOLDOWN, false);
        builder.add(FIXED_COOLDOWN_TICKS, 0);
    }
    private DamageSource attackeddamageSource;
    private Vec3d attackedEntityPos;
    private Entity attackedEntity;
    // 修改移动方法，冷却期间禁止移动
    @Override
    public void travel(Vec3d movementInput) {
        if (isAttackCooldown()) {
            // 完全停止所有移动
            super.travel(Vec3d.ZERO);
            return;
        }
        super.travel(movementInput);
    }

    // 尝试攻击时的处理
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
            // 如果处于冷却状态，取消攻击
            if (isAttackCooldown()) {
                return false;
            }

            boolean bl = mobTryAttack(world,target);
            if (bl) {
                // 触发攻击冷却
                cooldownTicks = 20; // 20刻冷却
                this.getDataTracker().set(ATTACK_COOLDOWN, true);
                float f = this.getWorld().getLocalDifficulty(this.getBlockPos()).getLocalDifficulty();
                if (this.getMainHandStack().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
                    target.setOnFireFor(2 * (int)f);
                }
            }
            return bl;
    }

    // 定义一个名为 mobTryAttack 的方法，该方法在 ServerWorld 环境下执行，对目标实体进行攻击尝试，返回一个布尔值表示攻击是否成功
    public boolean mobTryAttack(ServerWorld world, Entity target) {
        // 获取当前实体的攻击伤害属性值，并将其转换为 float 类型
        float f = (float)this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
        // 获取当前实体所持有的武器栈（ItemStack 对象，表示武器）
        ItemStack itemStack = this.getWeaponStack();
        // 尝试从武器的物品中获取伤害源；如果获取失败，则使用当前实体的默认攻击伤害源
        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.getDamageSources().mobAttack(this));
        // 根据世界、武器、目标实体、伤害源以及当前攻击伤害值，通过 EnchantmentHelper 计算实际的攻击伤害值（考虑附魔等因素对伤害的影响）
        f = EnchantmentHelper.getDamage(world, itemStack, target, damageSource, f);
        // 将武器物品根据目标实体、当前攻击伤害值以及伤害源计算出的额外攻击伤害值累加到 f 中
        f += itemStack.getItem().getBonusAttackDamage(target, f, damageSource);
        setAttackedEntity(target, damageSource);
        // 新增：应用固定效果（60秒冷却）
        if (!isFixedAbilityOnCooldown() && target instanceof LivingEntity livingTarget) {
            // 完全冻结实体移动
            livingTarget.setVelocity(Vec3d.ZERO);
            livingTarget.velocityModified = true;
            // 触发固定能力冷却
            this.getDataTracker().set(FIXED_ABILITY_COOLDOWN, true);
            this.getDataTracker().set(FIXED_COOLDOWN_TICKS, 60);
        }
        // 调用目标实体的 damage 方法，使其受到伤害，并返回一个布尔值表示目标实体是否受到了伤害（即攻击是否成功）
        boolean bl = target.damage(world, damageSource, f);
        if (bl) {
            this.triggerAnim("attack_controller", "attack");
            if (isFixedAbilityOnCooldown()) mobKnoc(target, damageSource);
            // 如果目标实体是生物实体（LivingEntity）
            if (target instanceof LivingEntity livingEntity) {
                // 调用武器栈的 postHit 方法，执行武器在击中生物实体后的相关逻辑（如消耗耐久度等）
                itemStack.postHit(livingEntity, this);
            }
            // 调用 EnchantmentHelper 的 onTargetDamaged 方法，处理目标实体受到伤害时的附魔相关逻辑（如触发特定附魔效果等）
            EnchantmentHelper.onTargetDamaged(world, target, damageSource);
            // 调用当前实体的 onAttacking 方法，执行攻击时的特定逻辑（如动画、状态更新等）
            this.onAttacking(target);
            // 播放当前实体的攻击音效
            this.playAttackSound();
        }

        return bl;
    }
    public void setAttackedEntity(Entity attackedEntity, DamageSource damageSource) {
        this.attackeddamageSource = damageSource;
        this.attackedEntity = attackedEntity;
        if (!isFixedAbilityOnCooldown()) this.attackedEntityPos = attackedEntity.getPos();
    }
    public void mobKnoc(Entity target, DamageSource damageSource) {
        // 计算当前实体对目标实体造成攻击时的击退力度
        float g = this.getAttackKnockbackAgainst(target, damageSource);
        // 如果击退力度大于 0 且目标实体是生物实体（LivingEntity）
        if (g > 0.0F && target instanceof LivingEntity livingEntity) {
            // 使生物实体根据当前实体的朝向（Yaw）计算出的击退方向和力度（g * 0.5F）进行击退
            livingEntity.takeKnockback(g * 0.5F, MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)), -MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)));
            // 将当前实体的速度在水平方向上乘以 0.6（实现攻击后的轻微后坐效果）
            this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
        }
    }
    @Override
    public void tick() {
        super.tick();

        // 服务端处理冷却逻辑
        if (!this.getWorld().isClient()) {
            // 新增：更新固定能力冷却
            if (isFixedAbilityOnCooldown()) {
                int cooldown = this.getDataTracker().get(FIXED_COOLDOWN_TICKS);
                if (this.attackedEntity != null && this.attackedEntity.isAlive()) {
                    if (cooldown > 40) {
                        attackedEntity.setVelocity(Vec3d.ZERO);
                        attackedEntity.velocityModified = true;
                        attackedEntity.setPosition(attackedEntityPos);
                        if (attackedEntity.isPlayer()) attackedEntity.teleportTo(new TeleportTarget((ServerWorld) this.getWorld(), attackedEntityPos, Vec3d.ZERO, attackedEntity.getYaw(), attackedEntity.getPitch(), TeleportTarget.NO_OP));
                    }
                    if (cooldown == 39) mobKnoc(attackedEntity, attackeddamageSource);
                }
                if (cooldown > 0) {
                    this.getDataTracker().set(FIXED_COOLDOWN_TICKS, cooldown - 1);
                } else {
                    this.getDataTracker().set(FIXED_ABILITY_COOLDOWN, false);
                }
            }
            if (isAttackCooldown()) {
                // 冷却期间禁止移动
                this.setVelocity(Vec3d.ZERO);
                this.velocityDirty = true;

                // 减少冷却时间
                if (cooldownTicks > 0) {
                    cooldownTicks--;
                } else {
                    // 冷却结束
                    this.getDataTracker().set(ATTACK_COOLDOWN, false);
                }
            }
        }
    }


    // 获取环境音效
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WARDEN_SNIFF;
    }

    // 获取受伤音效
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WARDEN_HURT;
    }
    // 获取实体类型
    @Override
    public EntityType<? extends XunShengEntity> getType() {
        return (EntityType<? extends XunShengEntity>)super.getType();
    }
    // 获取死亡音效
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WARDEN_DEATH;
    }

    // 获取脚步音效
    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_WARDEN_STEP;
    }

    // 播放脚步音效
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.getStepSound(), 10.0F, 1.0F);
    }
    // 新增：保存/读取冷却状态
    @Override
    public void readCustomData(ReadView nbt) {
        super.readCustomData(nbt);
        this.getDataTracker().set(FIXED_ABILITY_COOLDOWN, nbt.getBoolean("FixedCooldown", false));
        this.getDataTracker().set(FIXED_COOLDOWN_TICKS, nbt.getInt("FixedCooldownTicks", 0));
    }

    @Override
    public void writeCustomData(WriteView nbt) {
        super.writeCustomData(nbt);
        nbt.putBoolean("FixedCooldown", this.getDataTracker().get(FIXED_ABILITY_COOLDOWN));
        nbt.putInt("FixedCooldownTicks", this.getDataTracker().get(FIXED_COOLDOWN_TICKS));
    }
    public static DefaultAttributeContainer.Builder addAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.MAX_HEALTH, 20.0D).add(EntityAttributes.MOVEMENT_SPEED, 0.3F).add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0F).add(EntityAttributes.ATTACK_KNOCKBACK, 1.5F).add(EntityAttributes.ATTACK_DAMAGE, 2.0F).add(EntityAttributes.FOLLOW_RANGE, 24.0F).add(EntityAttributes.ARMOR, 0);
    }
}
