package com.kltyton.mob_battle.entity.witherskeletonking.summon;

import com.kltyton.mob_battle.effect.ModEffects;
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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
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

public class ShieldAxeWitherSkeletonEntity extends WitherSkeletonEntity implements GeoEntity, ModSkillEntityType, OwnedSummon {
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(ShieldAxeWitherSkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> ATTACK3_COOLDOWN = DataTracker.registerData(ShieldAxeWitherSkeletonEntity.class, TrackedDataHandlerRegistry.INTEGER);

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

    public ShieldAxeWitherSkeletonEntity(EntityType<? extends WitherSkeletonEntity> entityType, World world) {
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
        builder.add(ATTACK3_COOLDOWN, 8 * 20);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.birthTicks > 0) {
            this.birthTicks--;
        }
        if (!this.getWorld().isClient()) {
            tickMovementSpeed();
            if (getAttack3Cooldown() > 0 && !hasSkill()) {
                setAttack3Cooldown(getAttack3Cooldown() - 1);
            }
            if (this.shatterTicks > 0 && --this.shatterTicks <= 0) {
                finishShatterShield();
            }
            if (this.birthTicks > 0 || this.shatterTicks > 0) {
                this.setAiDisabled(true);
            } else if (!hasSkill()) {
                this.setAiDisabled(false);
            }
        }
    }

    private void tickMovementSpeed() {
        double speed = 0.45D;
        LivingEntity target = this.getTarget();
        if (target != null && this.distanceTo(target) <= 3.0D) {
            speed = 0.35D;
        }
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(speed);
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
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
        this.setAiDisabled(true);
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
                    this.setAiDisabled(false);
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
        if (target == null || !(this.getWorld() instanceof ServerWorld world) || !isValidSummonTarget(target)) {
            return;
        }
        target.timeUntilRegen = 0;
        if (target.damage(world, this.getDamageSources().mobAttack(this), damage) && stun) {
            target.addStatusEffect(new StatusEffectInstance(ModEffects.STUN_ENTRY, 20, 0), this);
        }
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (!this.shieldBroken && isDamageFromFront(source)) {
            this.blockedDamage += amount;
            this.playSound(SoundEvents.ITEM_SHIELD_BLOCK.value(), 1.0F, 0.9F + this.random.nextFloat() * 0.2F);
            if (isShieldBreakingWeapon(source) || this.blockedDamage >= 600.0F) {
                startShatterShield();
            }
            return false;
        }
        return super.damage(world, source, amount);
    }

    private boolean isDamageFromFront(DamageSource source) {
        Entity sourceEntity = source.getSource() == null ? source.getAttacker() : source.getSource();
        if (sourceEntity == null) {
            return false;
        }
        Vec3d toSource = sourceEntity.getPos().subtract(this.getPos());
        Vec3d horizontalSource = new Vec3d(toSource.x, 0.0D, toSource.z);
        if (horizontalSource.lengthSquared() < 1.0E-4D) {
            return false;
        }
        Vec3d look = this.getRotationVec(1.0F);
        Vec3d horizontalLook = new Vec3d(look.x, 0.0D, look.z);
        if (horizontalLook.lengthSquared() < 1.0E-4D) {
            return false;
        }
        return horizontalLook.normalize().dotProduct(horizontalSource.normalize()) > 0.0D;
    }

    private boolean isShieldBreakingWeapon(DamageSource source) {
        ItemStack weaponStack = source.getWeaponStack();
        return weaponStack != null && weaponStack.getItem() instanceof AxeItem;
    }

    private void startShatterShield() {
        if (this.shatterTicks > 0) {
            return;
        }
        this.shieldBroken = true;
        this.shatterTicks = 45;
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.playSound(SoundEvents.ITEM_SHIELD_BREAK.value(), 1.0F, 1.0F);
        this.triggerAnim("skill_controller", "shatter_shield");
    }

    private void finishShatterShield() {
        this.shatterTicks = 0;
        this.shieldBroken = false;
        this.blockedDamage = 0.0F;
        this.setHasSkill(false);
        this.setAiDisabled(false);
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
            LivingEntity target = this.getTarget();
            if (target == null) {
                return state.setAndContinue(WALK_ANIM);
            }
            return this.distanceTo(target) <= 3.0D ? state.setAndContinue(WALK_2_ANIM) : state.setAndContinue(RUN_ANIM);
        }
        return state.setAndContinue(IDLE_ANIM);
    }

    public boolean hasSkill() {
        return this.dataTracker.get(HAS_SKILL);
    }

    public void setHasSkill(boolean hasSkill) {
        this.dataTracker.set(HAS_SKILL, hasSkill);
    }

    public int getAttack3Cooldown() {
        return this.dataTracker.get(ATTACK3_COOLDOWN);
    }

    public void setAttack3Cooldown(int cooldown) {
        this.dataTracker.set(ATTACK3_COOLDOWN, cooldown);
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
                .add(EntityAttributes.ATTACK_DAMAGE, 175.0D)
                .add(EntityAttributes.ARMOR, 30.0D)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 20.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.35D);
    }
}
