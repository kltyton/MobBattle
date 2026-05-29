package com.kltyton.mob_battle.entity.villager.warriorvillager;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.ai.goal.GeneralProtectionVillagerGoal;
import com.kltyton.mob_battle.entity.irongolem.ModBaseIronGolemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

// 近战村民
public class WarriorVillager extends IronGolem implements GeoEntity, ModBaseIronGolemEntity {
    // 添加群体仇恨的检测范围（64格）
    private static final double ALERT_RANGE = 64.0;
    public WarriorVillager(EntityType<? extends IronGolem> entityType, Level world) {
        super(entityType, world);
        this.getNavigation().setCanFloat(true);
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        world.broadcastEntityEvent(this, EntityEvent.START_ATTACKING);
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float g = (int)f > 0 ? f / 2.0F + this.random.nextInt((int)f) : f;
        DamageSource damageSource = this.damageSources().mobAttack(this);
        boolean bl = target.hurtServer(world, damageSource, g);
        if (bl) {
            this.triggerAnim("attack_controller", "attack");
            // 获取目标的击退抗性
            double d = target instanceof LivingEntity livingEntity
                    ? livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)
                    : 0.0;
            double e = Math.max(0.0, 1.0 - d);
            target.setDeltaMovement(target.getDeltaMovement().add(0.1 * e, 0.0, 0.1 * e));
            EnchantmentHelper.doPostAttackEffects(world, target, damageSource);
        }
        return bl;
    }
    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        boolean bl = super.hurtServer(world, source, amount);
        if (bl && !world.isClientSide) {
            Entity attacker = source.getEntity();
            // 确保攻击者是有效生物且不是铁傀儡
            if (attacker instanceof LivingEntity livingAttacker) {
                this.alertOthers(livingAttacker);
            }
        }
        return bl;
    }

    private void alertOthers(LivingEntity attacker) {
        // 获取64格范围内所有铁傀儡
        List<IronGolem> golems = this.level().getEntitiesOfClass(
                IronGolem.class,
                this.getBoundingBox().inflate(ALERT_RANGE),
                golem -> golem != this && golem.isAlive()
        );

        for (IronGolem golem : golems) {
            // 跳过玩家创建的且攻击者是玩家的铁傀儡
            if (attacker instanceof AbstractGolem) {
                continue;
            }

            // 设置仇恨目标和愤怒时间
            golem.setPersistentAngerTarget(attacker.getUUID());
            golem.startPersistentAngerTimer();

            // 立即更新目标选择
            if (golem.getTarget() != attacker) {
                golem.setTarget(attacker);
            }
        }
    }
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    public static boolean checkWarriorSpawnRules(EntityType<WarriorVillager> warriorVillagerEntityType, ServerLevelAccessor world, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return world.getCurrentDifficultyAt(pos).getDifficulty() != Difficulty.PEACEFUL;
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>("main_controller", 5 ,this::animationController));
        controllerRegistrar.add(new AnimationController<>( "attack_controller",animTest -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK_ANIM));
    }
    private PlayState animationController(final AnimationTest<WarriorVillager> state) {
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
        return this.geoCache;
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this)); // 添加游泳AI
        this.targetSelector.addGoal(1, new GeneralProtectionVillagerGoal(this));
    }
    @Override
    protected PathNavigation createNavigation(Level world) {
        return new GroundPathNavigation(this, world); // 允许基础游泳
    }

}
