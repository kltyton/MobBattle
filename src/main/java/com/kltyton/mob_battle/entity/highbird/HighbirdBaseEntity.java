package com.kltyton.mob_battle.entity.highbird;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import com.kltyton.mob_battle.network.packet.HighbirdAttackPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public abstract class HighbirdBaseEntity extends HighbirdAndEggEntity {

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

    public HighbirdBaseEntity(EntityType<? extends HighbirdBaseEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }
    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && !(this instanceof HighbirdAdulthoodEntity)) {
            // ===== 饥饿值处理逻辑 =====
            if (this.isTame()) {
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
                    if (starveTicks >= getMaxStarveTicks()) this.kill((ServerLevel)this.level());
                    // 禁用AI
                    this.setNoAi(true);
                } else {
                    if (attackOwner) attackOwner = false;
                    starveTicks = 0; // 重置饥饿计时
                    // 确保AI在非饥饿状态可用（除非睡眠中）
                    if (!isSleeping) {
                        this.setNoAi(false);
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
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
                this.hasImpulse = true;
            }
        }
    }

    // 当实体尝试攻击目标时调用此方法
    public boolean performAttack(ServerLevel world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        // 获取当前实体的基础攻击力（来自属性系统）
        float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

        // 获取当前手持的武器物品（可能是剑、斧头等）
        ItemStack itemStack = this.getWeaponItem();

        // 根据武器获取伤害来源（DamageSource），如果武器没有定义，则使用默认的“生物攻击”来源
        DamageSource damageSource = Optional
                .ofNullable(itemStack.getItem().getDamageSource(this)) // 尝试从武器获取自定义伤害来源
                .orElse(this.damageSources().mobAttack(this));      // 否则使用默认的生物攻击来源

        // 应用附魔对伤害的加成（如锋利、亡灵杀手等）
        f = EnchantmentHelper.modifyDamage(world, itemStack, target, damageSource, f);

        // 应用武器的额外伤害（如三叉戟对水生生物的额外伤害）
        f += itemStack.getItem().getAttackDamageBonus(target, f, damageSource);
        if (attackOwner) f = 1;

        // 对目标实体造成伤害，返回是否成功造成伤害
        boolean bl = target.hurtServer(world, damageSource, f);

        // 如果伤害成功施加
        if (bl) {
            // 获取击退强度（由武器、附魔或实体属性决定）
            float g = this.getKnockback(target, damageSource);

            // 如果击退强度大于0，且目标是生物（LivingEntity）
            if (g > 0.0F && target instanceof LivingEntity livingEntity) {
                // 计算击退方向（基于攻击者的朝向）
                float knockbackX = Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
                float knockbackZ = -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));

                // 对目标施加击退效果（击退强度减半）
                livingEntity.knockback(g * 0.5F, knockbackX, knockbackZ);

                // 攻击者自身速度降低（模拟攻击后摇）
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            // 如果目标是生物，触发武器的“攻击后”效果（如剑的横扫、斧头的破盾）
            if (target instanceof LivingEntity livingEntity) {
                itemStack.hurtEnemy(livingEntity, this);
            }

            // 触发附魔的“目标受伤后”效果（如火焰附加、抢夺等）
            EnchantmentHelper.doPostAttackEffects(world, target, damageSource);

            // 触发实体自身的攻击回调（用于子类扩展，如狼的驯服经验）
            this.setLastHurtMob(target);

            // 播放攻击音效（如剑的挥砍声）
            this.playAttackSound();
        }

        // 返回是否成功造成伤害
        return false;
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
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
                this.onGround() &&
                !this.isAggressive();
    }
    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (this.isSleeping) {
            if (!this.forcedWakeUp) this.forcedWakeUp = true;
            wake();
        }
        return super.hurtServer(world, source, amount);
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
            this.setNoAi(true);
        }
    }

    // 唤醒实体
    protected void wake() {
        if (isSleeping) {
            this.isSleeping = false;
            this.triggerAnim("sleep_controller", "wake");
            this.setNoAi(false);
        }
    }

    // ===== 新增：喂食交互逻辑 =====
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 驯服状态下喂食甜浆果
        if (this.isTame() && stack.getItem() == getfoodItem()) {
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
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
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
                                if (this.level().isClientSide) {
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
        if (this.isDeadOrDying()) {
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
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        // 保存饥饿相关数据
        view.putInt("Hunger", hunger);
        view.putInt("HungerCooldown", hungerCooldown);
        view.putInt("StarveTicks", starveTicks);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        // 读取饥饿相关数据
        hunger = view.getIntOr("Hunger", getMaxHunger());
        hungerCooldown = view.getIntOr("HungerCooldown", 0);
        starveTicks = view.getIntOr("StarveTicks", 0);
    }
    @Override
    public void knockback(double strength, double x, double z) {
        if (!this.isDeadOrDying()) super.knockback(strength, x, z);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    /* ---------- TameableEntity 必须实现的抽象方法 ---------- */
    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
        return null;
    }
}