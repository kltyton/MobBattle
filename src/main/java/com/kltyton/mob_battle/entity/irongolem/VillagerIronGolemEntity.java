package com.kltyton.mob_battle.entity.irongolem;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.ai.goal.GeneralProtectionVillagerGoal;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.CombatEffectUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GolemRandomStrollInVillageGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveBackToVillageGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.OfferFlowerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VillagerIronGolemEntity extends IronGolem implements GeoEntity, ModBaseIronGolemEntity, ModSkillEntityType {
    public VillagerIronGolemEntity(EntityType<? extends IronGolem> entityType, Level world) {
        super(entityType, world);
        this.setNoAi(false);
        this.setHasSkill(false);
        this.setSkillCooldown(200);
    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 32.0F));
        this.goalSelector.addGoal(2, new MoveBackToVillageGoal(this, 0.6, false));
        this.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.6));
        this.goalSelector.addGoal(5, new OfferFlowerGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
        this.targetSelector.addGoal(1, new GeneralProtectionVillagerGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (entity, world) -> entity instanceof Enemy));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }
    //skill
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(VillagerIronGolemEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN = SynchedEntityData.defineId(VillagerIronGolemEntity.class, EntityDataSerializers.INT);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
        builder.define(SKILL_COOLDOWN, 400);
    }
    public boolean hasSkill() {
        return getEntityData().get(HAS_SKILL);
    }
    public int getSkillCooldown() {
        return getEntityData().get(SKILL_COOLDOWN);
    }

    public void setHasSkill(boolean hasSkill) {
        getEntityData().set(HAS_SKILL, hasSkill);
    }
    public void setSkillCooldown(int cooldown) {
        getEntityData().set(SKILL_COOLDOWN, cooldown);
    }

    public boolean canSkill() {
        if (!ModSkillEntityType.canSkill(this)) return false;
        return !this.level().isClientSide() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    public void performSkill() {
        this.setHasSkill(true);
        this.setNoAi(true);
        this.setSkillCooldown(200);
        this.triggerAnim("attack_controller", "attack2");
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (!hasSkill()) {
                this.setNoAi(false);
                // 冷却递减
                int cd = getSkillCooldown();
                if (cd > 0) setSkillCooldown(cd - 1);
            }
        }
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation ATTACK_ANIM_2 = RawAnimation.begin().thenPlay("attack2");
    @Override
    public void knockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isNoAi()) {
            super.knockback(strength, x, z);
        }
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        if (canSkill()) {
            performSkill();
            return true;
        }
        if (this.hasSkill()) return false;
        world.broadcastEntityEvent(this, EntityEvent.START_ATTACKING);
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float g = (int)f > 0 ? f / 2.0F + this.random.nextInt((int)f) : f;
        DamageSource damageSource = this.damageSources().mobAttack(this);
        boolean bl = target.hurtServer(world, damageSource, g);
        if (!this.hasSkill()) {
            this.triggerAnim("attack_controller", "attack");
            this.setHasSkill(true);
        }
        if (bl) {
            if (target instanceof LivingEntity livingEntity) {
                CombatEffectUtil.addStackingArmorPiercing(livingEntity, this);
            }
            double d = target instanceof LivingEntity livingEntity ? livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) : 0.0;
            double e = Math.max(0.0, 1.0 - d);
            target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.4F * e, 0.0));
            EnchantmentHelper.doPostAttackEffects(world, target, damageSource);
        }

        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return bl;
    }
    public boolean tryAttackBase(ServerLevel world, Entity target, float i) {
        world.broadcastEntityEvent(this, EntityEvent.START_ATTACKING);
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float g = (int)f > 0 ? f / 2.0F + this.random.nextInt((int)f) : f;
        DamageSource damageSource = this.damageSources().mobAttack(this);
        boolean bl = target.hurtServer(world, damageSource, g * i);
        if (bl) {
            if (target instanceof LivingEntity livingEntity) {
                CombatEffectUtil.addStackingArmorPiercing(livingEntity, this);
            }
            double d = target instanceof LivingEntity livingEntity ? livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) : 0.0;
            double e = Math.max(0.0, 1.0 - d);
            target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.4F * e, 0.0));
            EnchantmentHelper.doPostAttackEffects(world, target, damageSource);
        }

        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return bl;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>("main_controller", 5 ,this::animationController));
        controllerRegistrar.add(new AnimationController<>( "attack_controller",animTest -> {
                    if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED && this.hasSkill()) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "stop", this.getId()
                        ));
                    }
                    return PlayState.STOP;
                })
                .triggerableAnim("attack", ATTACK_ANIM)
                .triggerableAnim("attack2", ATTACK_ANIM_2)
                .setSoundKeyframeHandler(s -> {})
                        .setCustomInstructionKeyframeHandler(s -> {
                            Player player = ClientUtil.getClientPlayer();
                            if ("damage_1_5".equals(s.keyframeData().getInstructions())) {
                                player.playSound(SoundEvents.ANVIL_LAND, 1.0F, 1.0F);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "damage_1_5", this.getId()
                                ));
                            }
                            if ("damage_2".equals(s.keyframeData().getInstructions())) {
                                player.playSound(SoundEvents.ANVIL_LAND, 1.0F, 1.0F);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "damage_2", this.getId()
                                ));
                            }
                        })
        );
    }
    private PlayState animationController(final AnimationTest<VillagerIronGolemEntity> state) {
        if (state.isMoving()) {
            return state.setAndContinue(WALK_ANIM);
        } else {
            return state.setAndContinue(IDEA_ANIM);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
