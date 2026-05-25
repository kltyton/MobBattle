package com.kltyton.mob_battle.entity.witherskeletonking.summon;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.OwnedSummon;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.server.world.ServerWorld;
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

public class DualBladeWitherSkeletonEntity extends WitherSkeletonEntity implements GeoEntity, ModSkillEntityType, OwnedSummon {
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(DualBladeWitherSkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

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

    public DualBladeWitherSkeletonEntity(EntityType<? extends WitherSkeletonEntity> entityType, World world) {
        super(entityType, world);
        this.setHasSkill(false);
        this.setAiDisabled(true);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(8, new LookAtEntityGoal(this, LivingEntity.class, 10.0F));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false,
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
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.birthTicks > 0) {
            this.birthTicks--;
        }
        if (!this.getWorld().isClient()) {
            this.setAttacking(this.getTarget() != null);
            if (this.birthTicks > 0) {
                this.setAiDisabled(true);
            } else if (!hasSkill()) {
                this.setAiDisabled(false);
            }
        }
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
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
        this.setAiDisabled(true);
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
                this.setAiDisabled(false);
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    private void damageTarget(float damage) {
        LivingEntity target = this.getTarget();
        if (target == null || !(this.getWorld() instanceof ServerWorld world) || !isValidSummonTarget(target)) {
            return;
        }
        target.damage(world, this.getDamageSources().mobAttack(this), damage);
    }

    private void areaDamage(double radius, float damage) {
        if (!(this.getWorld() instanceof ServerWorld world)) {
            return;
        }
        for (LivingEntity target : EntityUtil.getNearbyEntity(this, LivingEntity.class, radius, false, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            if (!isValidSummonTarget(target)) {
                continue;
            }
            target.damage(world, this.getDamageSources().mobAttack(this), damage);
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
            return this.isAttacking() ? state.setAndContinue(RUN_ANIM) : state.setAndContinue(WALK_ANIM);
        }
        return state.setAndContinue(IDLE_ANIM);
    }

    public boolean hasSkill() {
        return this.dataTracker.get(HAS_SKILL);
    }

    public void setHasSkill(boolean hasSkill) {
        this.dataTracker.set(HAS_SKILL, hasSkill);
    }

    @Override
    public boolean canSkill() {
        return ModSkillEntityType.canSkill(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 2000.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.45D)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.7D)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 100.0D)
                .add(EntityAttributes.ARMOR, 25.0D)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 20.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.35D);
    }
}
