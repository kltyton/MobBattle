package com.kltyton.mob_battle.entity.piglingeneral;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.general.GeneralEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.CombatEffectUtil;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;

public class PiglinGeneralEntity extends AbstractPiglin implements GeneralEntity<PiglinGeneralEntity> {
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(PiglinGeneralEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_1 = SynchedEntityData.defineId(PiglinGeneralEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_2 = SynchedEntityData.defineId(PiglinGeneralEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_3 = SynchedEntityData.defineId(PiglinGeneralEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_4 = SynchedEntityData.defineId(PiglinGeneralEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_5 = SynchedEntityData.defineId(PiglinGeneralEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_6 = SynchedEntityData.defineId(PiglinGeneralEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RUSH_TICKS = SynchedEntityData.defineId(PiglinGeneralEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> NORMAL_ATTACK_DAMAGE = SynchedEntityData.defineId(PiglinGeneralEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> DYING_ANIMATION = SynchedEntityData.defineId(PiglinGeneralEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> DEATH_ANIMATION_TICKS = SynchedEntityData.defineId(PiglinGeneralEntity.class, EntityDataSerializers.INT);

    private static final int ATTACK2_MARK_DURATION = 15 * 20;
    private static final int ATTACK4_DURATION = 50 * 20;
    private static final int ATTACK5_MARK_DURATION = 5 * 20;

    private static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation NORMAL_ATTACK_ANIM_1 = RawAnimation.begin().thenPlay("attack1_1");
    private static final RawAnimation NORMAL_ATTACK_ANIM_2 = RawAnimation.begin().thenPlay("attack1_2");
    private static final RawAnimation NORMAL_ATTACK_ANIM_3 = RawAnimation.begin().thenPlay("attack1_3");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlay("death");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private LivingEntity goalTarget;

    @Nullable
    private Vec3 swordEnergyPos;

    private int swordEnergyPosAge = -1000;

    public PiglinGeneralEntity(EntityType<? extends AbstractPiglin> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 150;
        this.setHasSkill(false);
        this.setNoAi(false);
        this.setImmuneToZombification(true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new PiglinGeneralCombatGoal(this, 1.0D, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.75D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, LivingEntity.class, 10.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (target, world) -> EntityUtil.isValidCombatTarget(this, target) && !(target instanceof AbstractPiglin)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        entityInitDataTracker(builder);
        builder.define(SKILL_COOLDOWN_6, getMaxSkillCooldown_6());
        builder.define(RUSH_TICKS, 0);
        builder.define(NORMAL_ATTACK_DAMAGE, 100);
        builder.define(DYING_ANIMATION, false);
        builder.define(DEATH_ANIMATION_TICKS, 0);
    }
    @Override
    public void entityInitDataTracker(SynchedEntityData.Builder builder) {
        builder.define(getHasSkillKey(), false);
        builder.define(getCooldownKey1(), 1);
        builder.define(getCooldownKey2(), getMaxCooldownForSkill(2));
        builder.define(getCooldownKey3(), getMaxCooldownForSkill(3));
        builder.define(getCooldownKey4(), getMaxCooldownForSkill(4));
        builder.define(getCooldownKey5(), getMaxCooldownForSkill(5));
    }
    @Override
    public void tick() {
        super.tick();
        entityTick();

        if (!this.level().isClientSide() && isPlayingDeathAnimation()) {
            tickDeathAnimation();
            return;
        }

        if (!this.level().isClientSide()) {
            tickRush();

            if (!hasSkill()) {
                decrementCooldownIfPositive(SKILL_COOLDOWN_6);
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL) || isPlayingDeathAnimation()) {
            return false;
        }

        if (this.getHealth() - amount <= 0.0F) {
            startDeathAnimation();
            return true;
        }

        return super.hurtServer(world, source, amount);
    }

    private void startDeathAnimation() {
        this.setHealth(1.0F);
        this.setNoAi(true);
        this.setHasSkill(true);
        this.setPlayingDeathAnimation(true);
        this.setDeathAnimationTicks(22);
        this.triggerAnim("skill_controller", "death");
    }

    private void tickDeathAnimation() {
        int ticks = getDeathAnimationTicks();

        if (ticks > 0) {
            setDeathAnimationTicks(ticks - 1);
            this.setHealth(1.0F);
            return;
        }

        this.remove(Entity.RemovalReason.KILLED);
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!(target instanceof LivingEntity living)
                || living instanceof AbstractPiglin
                || !EntityUtil.isValidCombatTarget(this, living)
                || hasSkill()) {
            return false;
        }

        double distance = this.distanceTo(living);

        if (this.isRushing()) {
            if (distance <= 4.0D) {
                finishRushWithAttack2();
            }
            return true;
        }

        if (canSkill("attack7") && distance <= 6.0D) {
            performSkill("attack7");
            return true;
        }

        if (canSkill("attack6") && distance <= 5.0D) {
            performSkill("attack6");
            return true;
        }

        if (canSkill("attack5") && distance <= 12.0D) {
            performSkill("attack5");
            return true;
        }

        if (canSkill("attack4") && distance <= 20.0D) {
            performSkill("attack4");
            return true;
        }

        if (canSkill("attack3") && distance <= 7.0D) {
            performSkill("attack3");
            return true;
        }

        performNormalAttack();
        return true;
    }

    private void performNormalAttack() {
        setHasSkill(true);
        setNoAi(true);

        int index = this.random.nextInt(3);
        setNormalAttackDamage(index == 2 ? 150 : 100);

        triggerAnim("skill_controller", switch (index) {
            case 0 -> "attack1_1";
            case 1 -> "attack1_2";
            default -> "attack1_3";
        });
    }

    @Override
    public void performSkill(String skill) {
        GeneralEntity.super.performSkill(skill);
    }

    public void startRush() {
        setSkillCooldown("attack2");
        setRushTicks(1);
        this.getNavigation().stop();
    }

    private void tickRush() {
        if (!isRushing()) {
            return;
        }

        LivingEntity target = this.getTarget();
        if (target == null || target instanceof AbstractPiglin || !EntityUtil.isValidCombatTarget(this, target)) {
            stopRush();
            return;
        }

        int ticks = getRushTicks();
        if (ticks > 100) {
            stopRush();
            return;
        }

        double distance = this.distanceTo(target);
        if (distance <= 4.0D) {
            finishRushWithAttack2();
            return;
        }

        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.getNavigation().moveTo(target, 1.65D);

        Vec3 direction = target.position().subtract(this.position());
        Vec3 horizontalDirection = new Vec3(direction.x, 0.0D, direction.z);

        if (horizontalDirection.lengthSqr() > 1.0E-6D) {
            horizontalDirection = horizontalDirection.normalize();

            float yaw = (float) (Math.toDegrees(Math.atan2(horizontalDirection.z, horizontalDirection.x)) - 90.0F);

            this.setYRot(yaw);
            this.setYBodyRot(yaw);
            this.setYHeadRot(yaw);

            this.setDeltaMovement(horizontalDirection.scale(0.48D).add(0.0D, this.getDeltaMovement().y, 0.0D));
            this.hurtMarked = true;
        }

        setRushTicks(ticks + 1);
    }

    private void stopRush() {
        setRushTicks(0);
        this.getNavigation().stop();
    }

    private void finishRushWithAttack2() {
        stopRush();
        this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        this.hurtMarked = true;
        performSkill("attack2");
    }

    @Override
    public void runSkill_1_1(PiglinGeneralEntity entity) {
        normalAreaAttack(100.0F);
    }

    @Override
    public void runSkill_1_2(PiglinGeneralEntity entity) {
        normalAreaAttack(100.0F);
    }

    @Override
    public void runSkill_1_3(PiglinGeneralEntity entity) {
        normalAreaAttack(150.0F);
    }

    @Override
    public void runSkill_1(PiglinGeneralEntity entity) {
        normalAreaAttack(getNormalAttackDamage());
    }

    private void normalAreaAttack(float damage) {
        areaDamage((ServerLevel) level(), 4.0D, damage, 0, 0, true);
    }

    @Override
    public void runSkill_2(PiglinGeneralEntity entity) {
        areaDamage((ServerLevel) level(), 5.0D, 280.0F, 15, ATTACK2_MARK_DURATION, false);
    }

    @Override
    public void runSkill_3(PiglinGeneralEntity entity) {
        LivingEntity target = this.getTarget();
        if (target == null || !(this.level() instanceof ServerLevel world) || !EntityUtil.isValidCombatTarget(this, target)) {
            return;
        }

        markedDamage(world, target, 200.0F, 0, 0);
        target.setDeltaMovement(target.getDeltaMovement().x, 1.45D, target.getDeltaMovement().z);
        target.hurtMarked = true;
    }

    @Override
    public void runSkill_3_1(PiglinGeneralEntity entity) {
        LivingEntity target = this.getTarget();
        if (target == null || !(this.level() instanceof ServerLevel world) || !EntityUtil.isValidCombatTarget(this, target)) {
            return;
        }

        markedDamage(world, target, 250.0F, 0, 0);
        target.setDeltaMovement(target.getDeltaMovement().x * 0.25D, -1.65D, target.getDeltaMovement().z * 0.25D);
        target.hurtMarked = true;
    }

    @Override
    public void runSkill_3_2(PiglinGeneralEntity entity) {
        LivingEntity target = this.getTarget();
        if (target == null || !(this.level() instanceof ServerLevel world) || !EntityUtil.isValidCombatTarget(this, target)) {
            return;
        }

        teleportToUppercutTarget(world, target);
    }

    @Override
    public void runSkill_4(PiglinGeneralEntity entity) {
        for (LivingEntity living : EntityUtil.getNearbyEntity(this, LivingEntity.class, 20.0D, true, EntityUtil.TeamFilter.ALL)) {
            if (living == this || living.isAlliedTo(this)) {
                living.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, ATTACK4_DURATION, 4), this);
            } else {
                CombatEffectUtil.addPigSpiritMark(living, this, 10, ATTACK4_DURATION);
            }
        }
    }

    @Override
    public void runSkill_5(PiglinGeneralEntity entity) {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }

        Vec3 center = getSwordEnergyDamageCenter();
        AABB box = new AABB(center.subtract(4.0D, 4.0D, 4.0D), center.add(4.0D, 4.0D, 4.0D));

        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, box,
                target -> EntityUtil.isValidCombatTarget(this, target) && target.distanceToSqr(center) <= 16.0D)) {
            markedDamage(world, target, 320.0F, 50, ATTACK5_MARK_DURATION);
        }
    }

    @Override
    public void runSkill_6(PiglinGeneralEntity entity) {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }

        for (LivingEntity target : EntityUtil.getEntitiesInCone(this, LivingEntity.class, 5.0D, 85.0F, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            markedDamage(world, target, 300.0F, 0, 0);
        }
    }

    @Override
    public void runSkill_7(PiglinGeneralEntity entity) {
        areaDamage((ServerLevel) level(), 5.5D, 220.0F, 0, 0, false);
    }

    @Override
    public void runSkill_7_1(PiglinGeneralEntity entity) {
        areaDamage((ServerLevel) level(), 5.5D, 300.0F, 0, 0, false);
    }

    private void areaDamage(ServerLevel world, double radius, float damage, int markLayers, int markDurationTicks, boolean armorPiercing) {
        List<LivingEntity> targets = EntityUtil.getNearbyEntity(this, LivingEntity.class, radius, false, EntityUtil.TeamFilter.EXCLUDE_TEAM);

        for (LivingEntity target : targets) {
            if (markedDamage(world, target, damage, markLayers, markDurationTicks) && armorPiercing) {
                CombatEffectUtil.addStackingArmorPiercing(target, this);
            }
        }
    }

    private boolean markedDamage(ServerLevel world, LivingEntity target, float damage, int markLayers, int markDurationTicks) {
        MobEffectInstance mark = target.getEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
        float multiplier = mark == null ? 1.0F : 1.3F;

        DamageSource source = this.damageSources().mobAttack(this);
        boolean hit = target.hurtServer(world, source, damage * multiplier);

        if (hit && markLayers > 0) {
            CombatEffectUtil.addPigSpiritMark(target, this, markLayers, markDurationTicks);
        }

        return hit;
    }

    private void teleportToUppercutTarget(ServerLevel world, LivingEntity target) {
        Vec3 aboveTarget = target.position().add(0.0D, target.getBbHeight() + 0.6D, 0.0D);
        Vec3 fallback = target.position();
        Vec3 destination = canStandAt(world, aboveTarget) ? aboveTarget : fallback;
        this.teleportTo(destination.x, destination.y, destination.z);
    }

    private boolean canStandAt(ServerLevel world, Vec3 pos) {
        AABB box = this.getBoundingBox().move(pos.subtract(this.position()));
        return world.noCollision(this, box);
    }

    private Vec3 getSwordEnergyDamageCenter() {
        if (this.swordEnergyPos != null && this.tickCount - this.swordEnergyPosAge <= 10) {
            return this.swordEnergyPos;
        }

        Vec3 direction = this.getViewVector(1.0F).normalize();
        return this.getEyePosition().add(direction.scale(4.0D));
    }

    public void setSwordEnergyPos(Vec3 swordEnergyPos) {
        this.swordEnergyPos = swordEnergyPos;
        this.swordEnergyPosAge = this.tickCount;
    }

    @Override
    public Mob getEntity() {
        return this;
    }

    @Override
    public int getSkillCount() {
        return 6;
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
    public int getMaxSkillCooldown_1() {
        return 20 * 20;
    }

    @Override
    public int getMaxSkillCooldown_2() {
        return 15 * 20;
    }

    @Override
    public int getMaxSkillCooldown_3() {
        return 60 * 20;
    }

    @Override
    public int getMaxSkillCooldown_4() {
        return 30 * 20;
    }

    @Override
    public int getMaxSkillCooldown_5() {
        return 25 * 20;
    }

    public int getMaxSkillCooldown_6() {
        return 18 * 20;
    }

    @Override
    public void setSkillCooldown(String skill) {
        if ("attack7".equals(skill)) {
            this.entityData.set(SKILL_COOLDOWN_6, getMaxSkillCooldown_6());
            return;
        }

        GeneralEntity.super.setSkillCooldown(skill);
    }

    @Override
    public int getSkillCooldown(String skill) {
        if ("attack7".equals(skill)) {
            return this.entityData.get(SKILL_COOLDOWN_6);
        }

        return GeneralEntity.super.getSkillCooldown(skill);
    }

    public int getRushTicks() {
        return this.entityData.get(RUSH_TICKS);
    }

    public void setRushTicks(int ticks) {
        this.entityData.set(RUSH_TICKS, ticks);
    }

    public boolean isRushing() {
        return getRushTicks() > 0;
    }

    public int getNormalAttackDamage() {
        return this.entityData.get(NORMAL_ATTACK_DAMAGE);
    }

    public void setNormalAttackDamage(int damage) {
        this.entityData.set(NORMAL_ATTACK_DAMAGE, damage);
    }

    public boolean isPlayingDeathAnimation() {
        return this.entityData.get(DYING_ANIMATION);
    }

    public void setPlayingDeathAnimation(boolean dying) {
        this.entityData.set(DYING_ANIMATION, dying);
    }

    public int getDeathAnimationTicks() {
        return this.entityData.get(DEATH_ANIMATION_TICKS);
    }

    public void setDeathAnimationTicks(int ticks) {
        this.entityData.set(DEATH_ANIMATION_TICKS, ticks);
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
                .triggerableAnim("attack1_1", NORMAL_ATTACK_ANIM_1)
                .triggerableAnim("attack1_2", NORMAL_ATTACK_ANIM_2)
                .triggerableAnim("attack1_3", NORMAL_ATTACK_ANIM_3)
                .triggerableAnim("attack2", ATTACK_ANIM_2)
                .triggerableAnim("attack3", ATTACK_ANIM_3)
                .triggerableAnim("attack4", ATTACK_ANIM_4)
                .triggerableAnim("attack5", ATTACK_ANIM_5)
                .triggerableAnim("attack6", ATTACK_ANIM_6)
                .triggerableAnim("attack7", ATTACK_ANIM_7)
                .triggerableAnim("death", DEATH_ANIM)
                .setCustomInstructionKeyframeHandler(s -> {
                    String instruction = s.keyframeData().getInstructions().replaceAll("\\s+", "");

                    switch (instruction) {
                        case "runAttack1_1;" -> ClientPlayNetworking.send(new SkillPayload("attack1_1", this.getId()));
                        case "runAttack1_2;" -> ClientPlayNetworking.send(new SkillPayload("attack1_2", this.getId()));
                        case "runAttack1_3;" -> ClientPlayNetworking.send(new SkillPayload("attack1_3", this.getId()));
                        case "runAttack2;" -> ClientPlayNetworking.send(new SkillPayload("attack2", this.getId()));
                        case "runAttack3;" -> ClientPlayNetworking.send(new SkillPayload("attack3", this.getId()));
                        case "runAttack3_1;" -> ClientPlayNetworking.send(new SkillPayload("attack3_1", this.getId()));
                        case "runAttack3_2;" -> ClientPlayNetworking.send(new SkillPayload("attack3_2", this.getId()));
                        case "runAttack4;" -> ClientPlayNetworking.send(new SkillPayload("attack4", this.getId()));
                        case "runAttack5;" -> ClientPlayNetworking.send(new SkillPayload("attack5", this.getId()));
                        case "runAttack6;" -> ClientPlayNetworking.send(new SkillPayload("attack6", this.getId()));
                        case "runAttack7;" -> ClientPlayNetworking.send(new SkillPayload("attack7", this.getId()));
                        case "runAttack7_1;" -> ClientPlayNetworking.send(new SkillPayload("attack7_1", this.getId()));
                        default -> {
                        }
                    }
                }));
    }

    @Override
    public PlayState mainController(AnimationTest<?> event) {
        event.renderState().addGeckolibData(PiglinGeneralEntityRenderer.ENTITY_ID, this.getUUID());

        if (this.isDeadOrDying() || isPlayingDeathAnimation()) {
            return event.setAndContinue(DEATH_ANIM);
        }

        if (isRushing()) {
            return event.setAndContinue(RUN_ANIM);
        }

        if (hasSkill()) {
            return PlayState.CONTINUE;
        }

        if (event.isMoving()) {
            return event.setAndContinue(WALK_ANIM);
        }

        return event.setAndContinue(IDLE_ANIM);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getDeathSound() {
        return SoundEvents.PIGLIN_BRUTE_DEATH;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target instanceof AbstractPiglin
                || target == this
                || (target != null && !EntityUtil.isValidCombatTarget(this, target))) {
            target = null;
        }

        super.setTarget(target);
        this.goalTarget = target;
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        return this.goalTarget;
    }

    @Override
    protected boolean canHunt() {
        return false;
    }

    @Override
    public PiglinArmPose getArmPose() {
        return this.hasSkill() || this.isRushing() ? PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON : PiglinArmPose.DEFAULT;
    }

    @Override
    protected void playConvertedSound() {
        this.makeSound(SoundEvents.PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.31D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 12.0D)
                .add(Attributes.ATTACK_DAMAGE, 100.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.64D);
    }

    private static class PiglinGeneralCombatGoal extends Goal {
        private final PiglinGeneralEntity mob;
        private final double speed;
        private final boolean pauseWhenMobIdle;

        private Path path;
        private double targetX;
        private double targetY;
        private double targetZ;
        private int updateCountdownTicks;
        private int cooldown;
        private long lastUpdateTime;

        private PiglinGeneralCombatGoal(PiglinGeneralEntity mob, double speed, boolean pauseWhenMobIdle) {
            this.mob = mob;
            this.speed = speed;
            this.pauseWhenMobIdle = pauseWhenMobIdle;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            long time = this.mob.level().getGameTime();

            if (time - this.lastUpdateTime < 20L) {
                return false;
            }

            this.lastUpdateTime = time;

            LivingEntity target = this.mob.getTarget();
            if (!this.isValidTarget(target)) {
                return false;
            }

            this.path = this.mob.getNavigation().createPath(target, 0);
            return this.path != null || this.mob.isWithinMeleeAttackRange(target) || this.mob.distanceTo(target) <= 20.0D;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.mob.getTarget();

            if (!this.isValidTarget(target)) {
                return false;
            }

            if (this.mob.hasSkill() || this.mob.isPlayingDeathAnimation()) {
                return false;
            }

            if (this.mob.isRushing()) {
                return true;
            }

            if (!this.pauseWhenMobIdle) {
                return !this.mob.getNavigation().isDone();
            }

            if (!this.mob.isWithinHome(target.blockPosition())) {
                return false;
            }

            return !(target instanceof Player player && (player.isSpectator() || player.isCreative()));
        }

        @Override
        public void start() {
            if (this.path != null) {
                this.mob.getNavigation().moveTo(this.path, this.speed);
            }

            this.mob.setAggressive(true);
            this.updateCountdownTicks = 0;
            this.cooldown = 0;
        }

        @Override
        public void stop() {
            LivingEntity target = this.mob.getTarget();

            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
                this.mob.setTarget(null);
            }

            this.mob.setAggressive(false);

            if (!this.mob.isRushing()) {
                this.mob.getNavigation().stop();
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (!this.isValidTarget(target)) {
                this.mob.getNavigation().stop();
                return;
            }

            if (this.mob.hasSkill() || this.mob.isPlayingDeathAnimation()) {
                this.mob.getNavigation().stop();
                return;
            }

            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            double distance = this.mob.distanceTo(target);

            if (this.mob.isRushing()) {
                this.mob.getNavigation().stop();
                return;
            }

            if (distance > 4.0D && distance <= 20.0D && this.mob.canSkill("attack2")) {
                this.mob.getNavigation().stop();
                this.mob.startRush();
                return;
            }

            this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);

            if ((this.pauseWhenMobIdle || this.mob.getSensing().hasLineOfSight(target))
                    && this.updateCountdownTicks <= 0
                    && (
                    this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D
                            || target.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0D
                            || this.mob.getRandom().nextFloat() < 0.05F
            )) {
                this.targetX = target.getX();
                this.targetY = target.getY();
                this.targetZ = target.getZ();

                this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);

                double squaredDistance = this.mob.distanceToSqr(target);
                if (squaredDistance > 1024.0D) {
                    this.updateCountdownTicks += 10;
                } else if (squaredDistance > 256.0D) {
                    this.updateCountdownTicks += 5;
                }

                if (!this.mob.getNavigation().moveTo(target, this.speed)) {
                    this.updateCountdownTicks += 15;
                }

                this.updateCountdownTicks = this.adjustedTickDelay(this.updateCountdownTicks);
            }

            this.cooldown = Math.max(this.cooldown - 1, 0);
            this.attack(target);
        }

        private void attack(LivingEntity target) {
            if (this.canAttack(target)) {
                this.resetCooldown();
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget((ServerLevel) this.mob.level(), target);
            }
        }

        private void resetCooldown() {
            this.cooldown = this.adjustedTickDelay(20);
        }

        private boolean isCooledDown() {
            return this.cooldown <= 0;
        }

        private boolean canAttack(LivingEntity target) {
            return this.isCooledDown()
                    && this.mob.isWithinMeleeAttackRange(target)
                    && this.mob.getSensing().hasLineOfSight(target);
        }

        private boolean isValidTarget(@Nullable LivingEntity target) {
            return target != null
                    && target.isAlive()
                    && !(target instanceof AbstractPiglin)
                    && EntityUtil.isValidCombatTarget(this.mob, target);
        }
    }
}