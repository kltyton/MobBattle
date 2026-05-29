package com.kltyton.mob_battle.entity.witherskeletonking.summon;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.OwnedSummon;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DualBladeWitherSkeletonEntity extends WitherSkeleton implements GeoEntity, ModSkillEntityType, OwnedSummon {
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(DualBladeWitherSkeletonEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation BIRTH_ANIM = RawAnimation.begin().thenPlayAndHold("birth");
    private static final RawAnimation ATTACK_1_ANIM = RawAnimation.begin().thenPlay("attack1");
    private static final RawAnimation ATTACK_2_ANIM = RawAnimation.begin().thenPlay("attack2");
    private static final RawAnimation ATTACK_3_ANIM = RawAnimation.begin().thenPlay("attack3");
    private static final RawAnimation ATTACK_4_ANIM = RawAnimation.begin().thenPlay("attack4");
    private static final RawAnimation ATTACK_5_ANIM = RawAnimation.begin().thenPlay("attack5");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Nullable
    private LivingEntity summonOwner;
    private int birthTicks = 45;

    public DualBladeWitherSkeletonEntity(EntityType<? extends WitherSkeleton> entityType, Level world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setNoAi(true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, LivingEntity.class, 10.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (target, world) -> isValidSummonTarget(target)));
    }

    public void setSummonOwner(@Nullable LivingEntity summonOwner) {
        this.summonOwner = summonOwner;
        if (summonOwner != null) {
            EntityUtil.joinSameTeam(this, summonOwner);
        }
    }

    @Nullable
    @Override
    public LivingEntity getSummonOwner() {
        return this.summonOwner;
    }

    private boolean isValidSummonTarget(LivingEntity target) {
        return EntityUtil.isValidSummonCombatTarget(this, this.summonOwner, target);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.birthTicks > 0) {
            this.birthTicks--;
        }
        if (!this.level().isClientSide()) {
            this.setAggressive(this.getTarget() != null);
            if (this.birthTicks > 0) {
                this.setNoAi(true);
            } else if (!hasSkill()) {
                this.setNoAi(false);
            }
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!(target instanceof LivingEntity living)
                || !EntityUtil.isValidCombatTarget(this, living)
                || !isValidSummonTarget(living)
                || hasSkill()
                || this.birthTicks > 0
                || !canSkill()) {
            return false;
        }
        performAttack(1 + this.random.nextInt(5));
        return true;
    }

    private void performAttack(int attack) {
        this.setHasSkill(true);
        this.setNoAi(true);
        this.triggerAnim("skill_controller", "attack" + attack);
    }

    public boolean handleSkillPayload(String skillName) {
        switch (skillName) {
            case "attack1" -> damageTarget(100.0F);
            case "attack2" -> damageTarget(100.0F);
            case "attack3" -> areaDamage(3.0D, 180.0F);
            case "attack4" -> damageTarget(200.0F);
            case "attack5" -> damageTarget(120.0F);
            case "stop" -> {
                this.setHasSkill(false);
                this.setNoAi(false);
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    private void damageTarget(float damage) {
        LivingEntity target = this.getTarget();
        if (target == null || !(this.level() instanceof ServerLevel world) || !isValidSummonTarget(target)) {
            return;
        }
        target.hurtServer(world, this.damageSources().mobAttack(this), damage);
    }

    private void areaDamage(double radius, float damage) {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        for (LivingEntity target : EntityUtil.getNearbyEntity(this, LivingEntity.class, radius, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            if (!isValidSummonTarget(target)) {
                continue;
            }
            target.hurtServer(world, this.damageSources().mobAttack(this), damage);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 5, this::mainController));
        controllers.add(new AnimationController<>("skill_controller", 5, animTest -> {
            if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED && this.hasSkill()) {
                ClientPlayNetworking.send(new SkillPayload("stop", this.getId()));
            }
            return PlayState.STOP;
        })
                .triggerableAnim("attack1", ATTACK_1_ANIM)
                .triggerableAnim("attack2", ATTACK_2_ANIM)
                .triggerableAnim("attack3", ATTACK_3_ANIM)
                .triggerableAnim("attack4", ATTACK_4_ANIM)
                .triggerableAnim("attack5", ATTACK_5_ANIM)
                .setCustomInstructionKeyframeHandler(s -> {
                    String instruction = s.keyframeData().getInstructions().replaceAll("\\s+", "");
                    switch (instruction) {
                        case "runAttack1;" -> ClientPlayNetworking.send(new SkillPayload("attack1", this.getId()));
                        case "runAttack2;" -> ClientPlayNetworking.send(new SkillPayload("attack2", this.getId()));
                        case "runAttack3;" -> ClientPlayNetworking.send(new SkillPayload("attack3", this.getId()));
                        case "runAttack4;" -> ClientPlayNetworking.send(new SkillPayload("attack4", this.getId()));
                        case "runAttack5;" -> ClientPlayNetworking.send(new SkillPayload("attack5", this.getId()));
                        default -> {
                        }
                    }
                }));
    }

    private PlayState mainController(AnimationTest<?> state) {
        if (this.birthTicks > 0) {
            return state.setAndContinue(BIRTH_ANIM);
        }
        if (this.hasSkill()) {
            return PlayState.CONTINUE;
        }
        if (state.isMoving()) {
            return this.isAggressive() ? state.setAndContinue(RUN_ANIM) : state.setAndContinue(WALK_ANIM);
        }
        return state.setAndContinue(IDLE_ANIM);
    }

    public boolean hasSkill() {
        return this.entityData.get(HAS_SKILL);
    }

    public void setHasSkill(boolean hasSkill) {
        this.entityData.set(HAS_SKILL, hasSkill);
    }

    @Override
    public boolean canSkill() {
        return ModSkillEntityType.canSkill(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 2000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.45D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 100.0D)
                .add(Attributes.ARMOR, 25.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 20.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.35D);
    }
}
