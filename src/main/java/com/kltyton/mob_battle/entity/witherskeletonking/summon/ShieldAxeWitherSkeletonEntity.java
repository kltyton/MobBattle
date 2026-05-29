package com.kltyton.mob_battle.entity.witherskeletonking.summon;

import com.kltyton.mob_battle.effect.ModEffects;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ShieldAxeWitherSkeletonEntity extends WitherSkeleton implements GeoEntity, ModSkillEntityType, OwnedSummon {
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(ShieldAxeWitherSkeletonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> ATTACK3_COOLDOWN = SynchedEntityData.defineId(ShieldAxeWitherSkeletonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> WALK2_BLOCKING = SynchedEntityData.defineId(ShieldAxeWitherSkeletonEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation WALK_2_ANIM = RawAnimation.begin().thenLoop("walk2");
    private static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation BIRTH_ANIM = RawAnimation.begin().thenPlayAndHold("birth");
    private static final RawAnimation SHATTER_SHIELD_ANIM = RawAnimation.begin().thenPlay("shatter_shield");
    private static final RawAnimation ATTACK_1_ANIM = RawAnimation.begin().thenPlay("attack1");
    private static final RawAnimation ATTACK_2_ANIM = RawAnimation.begin().thenPlay("attack2");
    private static final RawAnimation ATTACK_3_ANIM = RawAnimation.begin().thenPlay("attack3");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Nullable
    private LivingEntity summonOwner;
    private int birthTicks = 45;
    private int shatterTicks;
    private float blockedDamage;
    private boolean shieldBroken;

    public ShieldAxeWitherSkeletonEntity(EntityType<? extends WitherSkeleton> entityType, Level world) {
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
        builder.define(ATTACK3_COOLDOWN, 8 * 20);
        builder.define(WALK2_BLOCKING, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.birthTicks > 0) {
            this.birthTicks--;
        }
        if (!this.level().isClientSide()) {
            this.setAggressive(this.getTarget() != null);
            updateWalk2BlockingState();
            tickMovementSpeed();
            if (getAttack3Cooldown() > 0 && !hasSkill()) {
                setAttack3Cooldown(getAttack3Cooldown() - 1);
            }
            if (this.shatterTicks > 0 && --this.shatterTicks <= 0) {
                finishShatterShield();
            }
            if (this.birthTicks > 0 || this.shatterTicks > 0) {
                this.setNoAi(true);
            } else if (!hasSkill()) {
                this.setNoAi(false);
            }
        }
    }

    private void updateWalk2BlockingState() {
        LivingEntity target = this.getTarget();
        boolean moving = this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4D;
        boolean blocking = target != null
                && !hasSkill()
                && !this.shieldBroken
                && this.birthTicks <= 0
                && this.shatterTicks <= 0
                && moving
                && this.isAggressive()
                && this.distanceTo(target) <= 3.0D;
        setWalk2Blocking(blocking);
    }

    private void tickMovementSpeed() {
        double speed = 0.45D;
        LivingEntity target = this.getTarget();
        if (target != null && this.distanceTo(target) <= 3.0D) {
            speed = 0.35D;
        }
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!(target instanceof LivingEntity living)
                || !EntityUtil.isValidCombatTarget(this, living)
                || !isValidSummonTarget(living)
                || hasSkill()
                || this.birthTicks > 0
                || this.shatterTicks > 0
                || !canSkill()) {
            return false;
        }
        if (getAttack3Cooldown() <= 0) {
            performAttack(3);
            setAttack3Cooldown(8 * 20);
            return true;
        }
        performAttack(1 + this.random.nextInt(2));
        return true;
    }

    private void performAttack(int attack) {
        this.setHasSkill(true);
        this.setNoAi(true);
        this.triggerAnim("skill_controller", "attack" + attack);
    }

    public boolean handleSkillPayload(String skillName) {
        switch (skillName) {
            case "attack1" -> damageTarget(175.0F, false);
            case "attack2" -> damageTarget(150.0F, true);
            case "attack3" -> damageTarget(250.0F, false);
            case "stop" -> {
                if (this.shieldBroken || this.shatterTicks > 0) {
                    finishShatterShield();
                } else {
                    this.setHasSkill(false);
                    this.setNoAi(false);
                }
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    private void damageTarget(float damage, boolean stun) {
        LivingEntity target = this.getTarget();
        if (target == null || !(this.level() instanceof ServerLevel world) || !isValidSummonTarget(target)) {
            return;
        }
        if (target.hurtServer(world, this.damageSources().mobAttack(this), damage) && stun) {
            target.addEffect(new MobEffectInstance(ModEffects.STUN_ENTRY, 20, 0), this);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (isWalk2Blocking() && isDamageFromFront(source)) {
            this.blockedDamage += amount;
            this.playSound(SoundEvents.SHIELD_BLOCK.value(), 1.0F, 0.9F + this.random.nextFloat() * 0.2F);
            if (isShieldBreakingWeapon(source) || this.blockedDamage >= 600.0F) {
                startShatterShield();
            }
            return false;
        }
        return super.hurtServer(world, source, amount);
    }

    private boolean isDamageFromFront(DamageSource source) {
        Entity sourceEntity = source.getDirectEntity() == null ? source.getEntity() : source.getDirectEntity();
        if (sourceEntity == null) {
            return false;
        }
        Vec3 toSource = sourceEntity.position().subtract(this.position());
        Vec3 horizontalSource = new Vec3(toSource.x, 0.0D, toSource.z);
        if (horizontalSource.lengthSqr() < 1.0E-4D) {
            return false;
        }
        Vec3 look = this.getViewVector(1.0F);
        Vec3 horizontalLook = new Vec3(look.x, 0.0D, look.z);
        if (horizontalLook.lengthSqr() < 1.0E-4D) {
            return false;
        }
        return horizontalLook.normalize().dot(horizontalSource.normalize()) > 0.0D;
    }

    private boolean isShieldBreakingWeapon(DamageSource source) {
        ItemStack weaponStack = source.getWeaponItem();
        return weaponStack != null && weaponStack.getItem() instanceof AxeItem;
    }

    private void startShatterShield() {
        if (this.shatterTicks > 0) {
            return;
        }
        this.shieldBroken = true;
        this.shatterTicks = 45;
        this.setHasSkill(true);
        this.setNoAi(true);
        this.playSound(SoundEvents.SHIELD_BREAK.value(), 1.0F, 1.0F);
        this.triggerAnim("skill_controller", "shatter_shield");
    }

    private void finishShatterShield() {
        this.shatterTicks = 0;
        this.shieldBroken = false;
        this.blockedDamage = 0.0F;
        this.setHasSkill(false);
        this.setNoAi(false);
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
                .triggerableAnim("shatter_shield", SHATTER_SHIELD_ANIM)
                .setCustomInstructionKeyframeHandler(s -> {
                    String instruction = s.keyframeData().getInstructions().replaceAll("\\s+", "");
                    switch (instruction) {
                        case "runAttack1;" -> ClientPlayNetworking.send(new SkillPayload("attack1", this.getId()));
                        case "runAttack2;" -> ClientPlayNetworking.send(new SkillPayload("attack2", this.getId()));
                        case "runAttack3;" -> ClientPlayNetworking.send(new SkillPayload("attack3", this.getId()));
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
            if (!this.isAggressive()) {
                return state.setAndContinue(WALK_ANIM);
            }
            return isWalk2Blocking() ? state.setAndContinue(WALK_2_ANIM) : state.setAndContinue(RUN_ANIM);
        }
        return state.setAndContinue(IDLE_ANIM);
    }

    public boolean hasSkill() {
        return this.entityData.get(HAS_SKILL);
    }

    public void setHasSkill(boolean hasSkill) {
        this.entityData.set(HAS_SKILL, hasSkill);
    }

    public int getAttack3Cooldown() {
        return this.entityData.get(ATTACK3_COOLDOWN);
    }

    public void setAttack3Cooldown(int cooldown) {
        this.entityData.set(ATTACK3_COOLDOWN, cooldown);
    }

    public boolean isWalk2Blocking() {
        return this.entityData.get(WALK2_BLOCKING);
    }

    public void setWalk2Blocking(boolean blocking) {
        this.entityData.set(WALK2_BLOCKING, blocking);
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
                .add(Attributes.ATTACK_DAMAGE, 175.0D)
                .add(Attributes.ARMOR, 30.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 20.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.35D);
    }
}
