package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.general.GeneralEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WitherSkeletonDogEntity extends WitherSkeletonEntity implements GeneralEntity<WitherSkeletonDogEntity> {
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(WitherSkeletonDogEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN_1 = DataTracker.registerData(WitherSkeletonDogEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_2 = DataTracker.registerData(WitherSkeletonDogEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_3 = DataTracker.registerData(WitherSkeletonDogEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_4 = DataTracker.registerData(WitherSkeletonDogEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_5 = DataTracker.registerData(WitherSkeletonDogEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ATTACK_1_ANIM = RawAnimation.begin().thenPlay("attack1");
    private static final RawAnimation ATTACK_2_ANIM = RawAnimation.begin().thenPlay("attack2");
    private static final RawAnimation ATTACK_3_ANIM = RawAnimation.begin().thenPlay("attack3");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public WitherSkeletonDogEntity(EntityType<? extends WitherSkeletonEntity> entityType, World world) {
        super(entityType, world);
        this.setHasSkill(false);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 0.8D));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (entity, world) -> entity != this && !entity.isTeammate(this)));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        entityInitDataTracker(builder);
    }

    @Override
    public void tick() {
        super.tick();
        entityTick();
        if (!this.getWorld().isClient()) {
            double speed = this.getTarget() == null ? 0.3D : 0.6D;
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(speed);
        }
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        if (!(target instanceof LivingEntity living) || living.isTeammate(this) || hasSkill() || !canSkill()) {
            return false;
        }
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.triggerAnim("skill_controller", "attack" + (1 + this.random.nextInt(3)));
        return true;
    }

    @Override
    public void runSkill_1(WitherSkeletonDogEntity entity) {
        LivingEntity target = this.getTarget();
        if (target == null || !(this.getWorld() instanceof ServerWorld world)) {
            return;
        }
        target.timeUntilRegen = 0;
        target.damage(world, this.getDamageSources().mobAttack(this), 80.0F);
        target.timeUntilRegen = 0;
        target.damage(world, this.getDamageSources().indirectMagic(this, this), 20.0F);
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
                .setCustomInstructionKeyframeHandler(s -> {
                    String instruction = s.keyframeData().getInstructions().replaceAll("\\s+", "");
                    if ("runAttack;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("attack", this.getId()));
                    }
                }));
    }

    public PlayState mainController(AnimationTest<?> state) {
        if (this.hasSkill()) {
            return PlayState.CONTINUE;
        }
        if (state.isMoving()) {
            return this.getTarget() == null ? state.setAndContinue(WALK_ANIM) : state.setAndContinue(RUN_ANIM);
        }
        return state.setAndContinue(IDLE_ANIM);
    }

    @Override
    public MobEntity getEntity() {
        return this;
    }

    @Override
    public int getSkillCount() {
        return 0;
    }

    @Override
    public TrackedData<Boolean> getHasSkillKey() {
        return HAS_SKILL;
    }

    @Override
    public TrackedData<Integer> getCooldownKey1() {
        return SKILL_COOLDOWN_1;
    }

    @Override
    public TrackedData<Integer> getCooldownKey2() {
        return SKILL_COOLDOWN_2;
    }

    @Override
    public TrackedData<Integer> getCooldownKey3() {
        return SKILL_COOLDOWN_3;
    }

    @Override
    public TrackedData<Integer> getCooldownKey4() {
        return SKILL_COOLDOWN_4;
    }

    @Override
    public TrackedData<Integer> getCooldownKey5() {
        return SKILL_COOLDOWN_5;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return WitherSkeletonEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 1500.0D)
                .add(EntityAttributes.FOLLOW_RANGE, 60.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.ATTACK_DAMAGE, 80.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.30D);
    }
}
