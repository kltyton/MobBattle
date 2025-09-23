package com.kltyton.mob_battle.entity.highbird;

import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import com.kltyton.mob_battle.network.packet.HighbirdAttackPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public abstract class HighbirdBaseEntity extends HighbirdAndEggEntity{

    /* ---------- 动画定义 ---------- */
    protected static final RawAnimation IDLE_ANIM   = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM   = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation DEATH_ANIM  = RawAnimation.begin().thenPlayAndHold("death");
    protected static final RawAnimation SLEEP_ANIM  = RawAnimation.begin().thenPlay("sleep").thenLoop("sleeping");
    protected static final RawAnimation WAKE_ANIM   = RawAnimation.begin().thenPlay("wake");
    protected static final RawAnimation SLEEPING_ANIM = RawAnimation.begin().thenLoop("sleeping");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public boolean isSleeping = false;
    public boolean forcedWakeUp = false;
    public boolean farstAttack = false;
    public boolean attackOwner = false;
    public int excitedTime = 1200;
    /* ========== 新增饥饿机制相关字段 ========== */
    public int hunger = getMaxHunger();        // 当前饥饿值（0-20）
    public int hungerCooldown = 0; // 饥饿消耗计时器（每600刻触发）
    public int starveTicks = 0;    // 饥饿值为0的持续时间计数
    public Item getfoodItem() {
        return Items.SWEET_BERRIES;
    }
    public int getMaxHunger() {
        return 20;
    }
    public int getMaxHungerCooldown() {
        return 600;
    }
    public int getMaxStarveTicks() {
        return 2400;
    }
    public int getMaxFood() {
        return 2;
    }
    public int getMaxFoodHealth() {
        return 20;
    }

    public HighbirdBaseEntity(EntityType<? extends HighbirdBaseEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));
    }
    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient && !(this instanceof HighbirdAdulthoodEntity)) {
            // ===== 饥饿值处理逻辑 =====
            if (this.isTamed()) {
                // 每600刻减少1点饥饿值
                if (++hungerCooldown >= getMaxHungerCooldown()) {
                    hungerCooldown = 0;
                    if (hunger > 0) hunger--;
                }

                // 处理饥饿值为0的状态
                if (hunger <= 0) {
                    starveTicks++;
                    if (!attackOwner) attackOwner = true;
                    // 超过120刻则死亡
                    if (starveTicks >= getMaxStarveTicks()) this.kill((ServerWorld)this.getWorld());
                    // 禁用AI
                    this.setAiDisabled(true);
                } else {
                    if (attackOwner) attackOwner = false;
                    starveTicks = 0; // 重置饥饿计时
                    // 确保AI在非饥饿状态可用（除非睡眠中）
                    if (!isSleeping) {
                        this.setAiDisabled(false);
                    }
                }
            }
            if (forcedWakeUp) {
                if (excitedTime > 0) {
                    excitedTime--;
                } else {
                    forcedWakeUp = false;
                    excitedTime = 1200;
                }
            }
            if (shouldSleep() && !isSleeping && !forcedWakeUp) {
                startSleeping();
            }
            if (shouldWakeUp() && isSleeping) {
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

    // 当实体尝试攻击目标时调用此方法
    public boolean performAttack(ServerWorld world, Entity target) {
        // 获取当前实体的基础攻击力（来自属性系统）
        float f = (float) this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);

        // 获取当前手持的武器物品（可能是剑、斧头等）
        ItemStack itemStack = this.getWeaponStack();

        // 根据武器获取伤害来源（DamageSource），如果武器没有定义，则使用默认的“生物攻击”来源
        DamageSource damageSource = Optional
                .ofNullable(itemStack.getItem().getDamageSource(this)) // 尝试从武器获取自定义伤害来源
                .orElse(this.getDamageSources().mobAttack(this));      // 否则使用默认的生物攻击来源

        // 应用附魔对伤害的加成（如锋利、亡灵杀手等）
        f = EnchantmentHelper.getDamage(world, itemStack, target, damageSource, f);

        // 应用武器的额外伤害（如三叉戟对水生生物的额外伤害）
        f += itemStack.getItem().getBonusAttackDamage(target, f, damageSource);
        if (attackOwner) f = 1;

        // 对目标实体造成伤害，返回是否成功造成伤害
        boolean bl = target.damage(world, damageSource, f);

        // 如果伤害成功施加
        if (bl) {
            // 获取击退强度（由武器、附魔或实体属性决定）
            float g = this.getAttackKnockbackAgainst(target, damageSource);

            // 如果击退强度大于0，且目标是生物（LivingEntity）
            if (g > 0.0F && target instanceof LivingEntity livingEntity) {
                // 计算击退方向（基于攻击者的朝向）
                float knockbackX = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0));
                float knockbackZ = -MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0));

                // 对目标施加击退效果（击退强度减半）
                livingEntity.takeKnockback(g * 0.5F, knockbackX, knockbackZ);

                // 攻击者自身速度降低（模拟攻击后摇）
                this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
            }

            // 如果目标是生物，触发武器的“攻击后”效果（如剑的横扫、斧头的破盾）
            if (target instanceof LivingEntity livingEntity) {
                itemStack.postHit(livingEntity, this);
            }

            // 触发附魔的“目标受伤后”效果（如火焰附加、抢夺等）
            EnchantmentHelper.onTargetDamaged(world, target, damageSource);

            // 触发实体自身的攻击回调（用于子类扩展，如狼的驯服经验）
            this.onAttacking(target);

            // 播放攻击音效（如剑的挥砍声）
            this.playAttackSound();
        }

        // 返回是否成功造成伤害
        return false;
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        // 如果正在睡觉，先醒来再攻击
        if (isSleeping()) {
            wake();
        }
        if (!farstAttack) {
            farstAttack = true;
            return false;
        } else {
            farstAttack = false;
            this.triggerAnim("attack_controller", "attack");
            return true;
        }
    }


    // 检查睡觉条件
    public boolean shouldSleep() {
        return !this.isDay() &&
                this.isOnGround() &&
                !this.isAttacking();
    }
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (this.isSleeping) {
            if (!this.forcedWakeUp) this.forcedWakeUp = true;
            wake();
        }
        return super.damage(world, source, amount);
    }
    // 检查醒来条件
    protected boolean shouldWakeUp() {
        return this.isDay();
    }

    // 开始睡眠
    public void startSleeping() {
        if (!isSleeping) {
            isSleeping = true;
            this.triggerAnim("sleep_controller", "sleep");
            this.getNavigation().stop();
            this.setAiDisabled(true);
        }
    }

    // 唤醒实体
    protected void wake() {
        if (isSleeping) {
            this.isSleeping = false;
            this.triggerAnim("sleep_controller", "wake");
            this.setAiDisabled(false);
        }
    }

    // ===== 新增：喂食交互逻辑 =====
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // 驯服状态下喂食甜浆果
        if (this.isTamed() && stack.getItem() == getfoodItem()) {
            if (hunger < getMaxHunger()) {
                // 增加饥饿值（上限20）
                hunger = Math.min(getMaxHunger(), hunger + getMaxFood());
                // 重置饥饿惩罚计时
                starveTicks = 0;
                // 重新启用AI（如果因饥饿被禁用）
                //if (!this.isSleeping) this.setAiDisabled(false);
                // 恢复生命值（20点，但不超过最大生命值）
                float newHealth = Math.min(this.getMaxHealth(), this.getHealth() + getMaxFoodHealth());
                this.setHealth(newHealth);

                // 消耗物品（非创造模式）
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 主控制器：负责所有常规状态
        controllers.add(new AnimationController<>("main_controller", 5, this::mainController));
        // 攻击控制器
        controllers.add(
                new AnimationController<>("attack_controller", state ->
                    PlayState.STOP
                )
                        .triggerableAnim("attack", ATTACK_ANIM)
                        .setCustomInstructionKeyframeHandler(event -> {
                            // 检查关键帧指令是否匹配
                            if ("runAttack;".equals(event.keyframeData().getInstructions())) {
                                if (this.getWorld().isClient) {
                                    // 发送攻击数据包到服务端
                                    ClientPlayNetworking.send(new HighbirdAttackPayload(
                                            this.getId()
                                    ));
                                }
                            }
                        }));
        controllers.add(
                new AnimationController<>("sleep_controller", state -> {
                    if (state.isCurrentAnimation(SLEEP_ANIM)) {
                        isSleeping = true;
                        return PlayState.CONTINUE;
                    }
                    return PlayState.STOP;
                })
                        .triggerableAnim("sleep", SLEEP_ANIM)
                        .triggerableAnim("wake", WAKE_ANIM)
        );
    }

    private PlayState mainController(final AnimationTest<HighbirdBaseEntity> event) {
        if (!isDay()) return event.setAndContinue(SLEEPING_ANIM);
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
    // ===== 新增：NBT数据持久化 =====
    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        // 保存饥饿相关数据
        view.putInt("Hunger", hunger);
        view.putInt("HungerCooldown", hungerCooldown);
        view.putInt("StarveTicks", starveTicks);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        // 读取饥饿相关数据
        hunger = view.getInt("Hunger", getMaxHunger());
        hungerCooldown = view.getInt("HungerCooldown", 0);
        starveTicks = view.getInt("StarveTicks", 0);
    }
    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (!this.isDead()) super.takeKnockback(strength, x, z);
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