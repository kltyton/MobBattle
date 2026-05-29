package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.OwnedSummon;
import com.kltyton.mob_battle.entity.general.GeneralEntity;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WitherSkeletonDogEntity extends WitherSkeleton implements GeneralEntity<WitherSkeletonDogEntity>, OwnedSummon {
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(WitherSkeletonDogEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_1 = SynchedEntityData.defineId(WitherSkeletonDogEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_2 = SynchedEntityData.defineId(WitherSkeletonDogEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_3 = SynchedEntityData.defineId(WitherSkeletonDogEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_4 = SynchedEntityData.defineId(WitherSkeletonDogEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_5 = SynchedEntityData.defineId(WitherSkeletonDogEntity.class, EntityDataSerializers.INT);
    private static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ATTACK_1_ANIM = RawAnimation.begin().thenPlay("attack1");
    private static final RawAnimation ATTACK_2_ANIM = RawAnimation.begin().thenPlay("attack2");
    private static final RawAnimation ATTACK_3_ANIM = RawAnimation.begin().thenPlay("attack3");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Nullable
    private LivingEntity summonOwner;

    public WitherSkeletonDogEntity(EntityType<? extends WitherSkeleton> entityType, Level world) {
        super(entityType, world);
        this.setHasSkill(false);
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
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (entity, world) -> isValidSummonTarget(entity)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        entityInitDataTracker(builder);
    }

    @Override
    public void tick() {
        super.tick();
        entityTick();
        if (!this.level().isClientSide()) {
            this.setAggressive(this.getTarget() != null);
            double speed = this.getTarget() == null ? 0.3D : 0.6D;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!(target instanceof LivingEntity living) || !isValidSummonTarget(living) || hasSkill() || !canSkill()) {
            return false;
        }
        this.setHasSkill(true);
        this.setNoAi(true);
        this.triggerAnim("skill_controller", "attack" + (1 + this.random.nextInt(3)));
        return true;
    }

    @Override
    public void runSkill_1(WitherSkeletonDogEntity entity) {
        LivingEntity target = this.getTarget();
        if (target == null || !isValidSummonTarget(target) || !(this.level() instanceof ServerLevel world)) {
            return;
        }
        target.hurtServer(world, this.damageSources().mobAttack(this), 80.0F);
        target.hurtServer(world, this.damageSources().indirectMagic(this, this), 20.0F);
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
            return this.isAggressive() ? state.setAndContinue(RUN_ANIM) : state.setAndContinue(WALK_ANIM);
        }
        return state.setAndContinue(IDLE_ANIM);
    }

    @Override
    public Mob getEntity() {
        return this;
    }

    @Override
    public int getSkillCount() {
        return 0;
    }

    @Override
    public EntityDataAccessor<Boolean> getHasSkillKey() {
        return HAS_SKILL;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey1() {
        return SKILL_COOLDOWN_1;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey2() {
        return SKILL_COOLDOWN_2;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey3() {
        return SKILL_COOLDOWN_3;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey4() {
        return SKILL_COOLDOWN_4;
    }

    @Override
    public EntityDataAccessor<Integer> getCooldownKey5() {
        return SKILL_COOLDOWN_5;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return WitherSkeleton.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1500.0D)
                .add(Attributes.FOLLOW_RANGE, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 80.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.30D);
    }
}
