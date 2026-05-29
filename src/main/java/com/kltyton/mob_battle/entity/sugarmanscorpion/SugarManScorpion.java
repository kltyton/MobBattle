package com.kltyton.mob_battle.entity.sugarmanscorpion;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public class SugarManScorpion extends Monster implements GeoEntity, ModSkillEntityType {
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation ATTACK_ANIM_2 = RawAnimation.begin().thenPlay("attack_2");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public int AttackTime = 0;
    public SugarManScorpion(EntityType<? extends Monster> entityType, Level world) {
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
    protected void registerGoals() {
        // 把自杀治疗目标放在高优先级（根据你希望覆盖其他行为来调整优先级）
        this.goalSelector.addGoal(1, new SuicideHealGoal(this, 1.2D, 30.0D));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false)); // 添加僵尸攻击目标
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0)); // 添加远距离游荡目标
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F)); // 添加看向玩家的目标
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this)); // 添加环顾四周的目标
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(ZombifiedPiglin.class)); // 添加复仇目标
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true)); // 添加主动攻击玩家目标
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false)); // 添加攻击村民目标
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true)); // 添加攻击铁傀儡目标
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!canSkill()) return false;
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        ItemStack itemStack = this.getWeaponItem();
        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.damageSources().mobAttack(this));
        f = EnchantmentHelper.modifyDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getAttackDamageBonus(target, f, damageSource);
        if (AttackTime < 3) {
            AttackTime++;
        } else {
            AttackTime = 0;
            boolean bl2 = target.hurtServer(world, this.damageSources().magic(), 20.0f);
            if (bl2) {
                this.triggerAnim("attack_controller", "attack_2");
                if (target instanceof LivingEntity livingEntity) livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 2));
                float g = this.getKnockback(target, damageSource);
                if (g > 0.0F && target instanceof LivingEntity livingEntity) {
                    livingEntity.knockback(g * 0.5F, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
                }

                if (target instanceof LivingEntity livingEntity) {
                    itemStack.hurtEnemy(livingEntity, this);
                }

                EnchantmentHelper.doPostAttackEffects(world, target, damageSource);
                this.setLastHurtMob(target);
                this.playAttackSound();
            }
            return bl2;
        }
        boolean bl = target.hurtServer(world, damageSource, f);
        if (bl) {
            this.triggerAnim("attack_controller", "attack");
            float g = this.getKnockback(target, damageSource);
            if (g > 0.0F && target instanceof LivingEntity livingEntity) {
                livingEntity.knockback(g * 0.5F, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof LivingEntity livingEntity) {
                itemStack.hurtEnemy(livingEntity, this);
            }

            EnchantmentHelper.doPostAttackEffects(world, target, damageSource);
            this.setLastHurtMob(target);
            this.playAttackSound();
        }

        return bl;
    }
    public static AttributeSupplier.Builder createSugarManScorpionAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    public boolean canSkill() {
        return ModSkillEntityType.canSkill(this);
    }
}
