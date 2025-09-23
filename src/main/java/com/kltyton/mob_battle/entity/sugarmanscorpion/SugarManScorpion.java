package com.kltyton.mob_battle.entity.sugarmanscorpion;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
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

public class SugarManScorpion extends HostileEntity implements GeoEntity {
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation ATTACK_ANIM_2 = RawAnimation.begin().thenPlay("attack_2");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public int AttackTime = 0;
    public SugarManScorpion(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 5 ,this::animationController));
        controllers.add(new AnimationController<>( "attack_controller",animTest -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK_ANIM)
                .triggerableAnim("attack_2", ATTACK_ANIM_2));
    }
    private PlayState animationController(final AnimationTest<SugarManScorpion> state) {
        if (state.isMoving()) {
            // 移动状态时播放行走动画
            return state.setAndContinue(WALK_ANIM);
        } else {
            // 空闲状态时播放待机动画
            return state.setAndContinue(IDEA_ANIM);
        }
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache; // 返回之前创建的 geoCache 缓存实例
    }
    @Override
    protected void initGoals() {
        // 把自杀治疗目标放在高优先级（根据你希望覆盖其他行为来调整优先级）
        this.goalSelector.add(1, new SuicideHealGoal(this, 1.2D, 30.0D));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false)); // 添加僵尸攻击目标
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0)); // 添加远距离游荡目标
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F)); // 添加看向玩家的目标
        this.goalSelector.add(8, new LookAroundGoal(this)); // 添加环顾四周的目标
        this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge(ZombifiedPiglinEntity.class)); // 添加复仇目标
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true)); // 添加主动攻击玩家目标
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false)); // 添加攻击村民目标
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true)); // 添加攻击铁傀儡目标
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        float f = (float)this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
        ItemStack itemStack = this.getWeaponStack();
        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.getDamageSources().mobAttack(this));
        f = EnchantmentHelper.getDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getBonusAttackDamage(target, f, damageSource);
        if (AttackTime < 3) {
            AttackTime++;
        } else {
            AttackTime = 0;
            boolean bl2 = target.damage(world, this.getDamageSources().magic(), 20.0f);
            if (bl2) {
                this.triggerAnim("attack_controller", "attack_2");
                if (target instanceof LivingEntity livingEntity) livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 2));
                float g = this.getAttackKnockbackAgainst(target, damageSource);
                if (g > 0.0F && target instanceof LivingEntity livingEntity) {
                    livingEntity.takeKnockback(g * 0.5F, MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)), -MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)));
                    this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
                }

                if (target instanceof LivingEntity livingEntity) {
                    itemStack.postHit(livingEntity, this);
                }

                EnchantmentHelper.onTargetDamaged(world, target, damageSource);
                this.onAttacking(target);
                this.playAttackSound();
            }
            return bl2;
        }
        boolean bl = target.damage(world, damageSource, f);
        if (bl) {
            this.triggerAnim("attack_controller", "attack");
            float g = this.getAttackKnockbackAgainst(target, damageSource);
            if (g > 0.0F && target instanceof LivingEntity livingEntity) {
                livingEntity.takeKnockback(g * 0.5F, MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)), -MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)));
                this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof LivingEntity livingEntity) {
                itemStack.postHit(livingEntity, this);
            }

            EnchantmentHelper.onTargetDamaged(world, target, damageSource);
            this.onAttacking(target);
            this.playAttackSound();
        }

        return bl;
    }
    public static DefaultAttributeContainer.Builder createSugarManScorpionAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 50.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 20.0)
                .add(EntityAttributes.STEP_HEIGHT, 1.0);
    }
}
