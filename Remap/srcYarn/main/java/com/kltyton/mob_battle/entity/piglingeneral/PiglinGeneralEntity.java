package com.kltyton.mob_battle.entity.piglingeneral;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.general.GeneralEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.CombatEffectUtil;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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

public class PiglinGeneralEntity extends AbstractPiglinEntity implements GeneralEntity<PiglinGeneralEntity> {
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(PiglinGeneralEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN_1 = DataTracker.registerData(PiglinGeneralEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_2 = DataTracker.registerData(PiglinGeneralEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_3 = DataTracker.registerData(PiglinGeneralEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_4 = DataTracker.registerData(PiglinGeneralEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_5 = DataTracker.registerData(PiglinGeneralEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_6 = DataTracker.registerData(PiglinGeneralEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> RUSH_TICKS = DataTracker.registerData(PiglinGeneralEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> NORMAL_ATTACK_DAMAGE = DataTracker.registerData(PiglinGeneralEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> DYING_ANIMATION = DataTracker.registerData(PiglinGeneralEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> DEATH_ANIMATION_TICKS = DataTracker.registerData(PiglinGeneralEntity.class, TrackedDataHandlerRegistry.INTEGER);

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
    private Vec3d swordEnergyPos;

    private int swordEnergyPosAge = -1000;

    public PiglinGeneralEntity(EntityType<? extends AbstractPiglinEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 150;
        this.setHasSkill(false);
        this.setAiDisabled(false);
        this.setImmuneToZombification(true);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new PiglinGeneralCombatGoal(this, 1.0D, true));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 0.75D));
        this.goalSelector.add(8, new LookAtEntityGoal(this, LivingEntity.class, 10.0F));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (target, world) -> EntityUtil.isValidCombatTarget(this, target) && !(target instanceof AbstractPiglinEntity)));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        entityInitDataTracker(builder);
        builder.add(SKILL_COOLDOWN_6, getMaxSkillCooldown_6());
        builder.add(RUSH_TICKS, 0);
        builder.add(NORMAL_ATTACK_DAMAGE, 100);
        builder.add(DYING_ANIMATION, false);
        builder.add(DEATH_ANIMATION_TICKS, 0);
    }
    @Override
    public void entityInitDataTracker(DataTracker.Builder builder) {
        builder.add(getHasSkillKey(), false);
        builder.add(getCooldownKey1(), 1);
        builder.add(getCooldownKey2(), getMaxCooldownForSkill(2));
        builder.add(getCooldownKey3(), getMaxCooldownForSkill(3));
        builder.add(getCooldownKey4(), getMaxCooldownForSkill(4));
        builder.add(getCooldownKey5(), getMaxCooldownForSkill(5));
    }
    @Override
    public void tick() {
        super.tick();
        entityTick();

        if (!this.getWorld().isClient() && isPlayingDeathAnimation()) {
            tickDeathAnimation();
            return;
        }

        if (!this.getWorld().isClient()) {
            tickRush();

            if (!hasSkill()) {
                decrementCooldownIfPositive(SKILL_COOLDOWN_6);
            }
        }
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (source.isOf(DamageTypes.FALL) || isPlayingDeathAnimation()) {
            return false;
        }

        if (this.getHealth() - amount <= 0.0F) {
            startDeathAnimation();
            return true;
        }

        return super.damage(world, source, amount);
    }

    private void startDeathAnimation() {
        this.setHealth(1.0F);
        this.setAiDisabled(true);
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
    public boolean tryAttack(ServerWorld world, Entity target) {
        if (!(target instanceof LivingEntity living)
                || living instanceof AbstractPiglinEntity
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
        setAiDisabled(true);

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
        if (target == null || target instanceof AbstractPiglinEntity || !EntityUtil.isValidCombatTarget(this, target)) {
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

        this.getLookControl().lookAt(target, 30.0F, 30.0F);
        this.getNavigation().startMovingTo(target, 1.65D);

        Vec3d direction = target.getPos().subtract(this.getPos());
        Vec3d horizontalDirection = new Vec3d(direction.x, 0.0D, direction.z);

        if (horizontalDirection.lengthSquared() > 1.0E-6D) {
            horizontalDirection = horizontalDirection.normalize();

            float yaw = (float) (Math.toDegrees(Math.atan2(horizontalDirection.z, horizontalDirection.x)) - 90.0F);

            this.setYaw(yaw);
            this.setBodyYaw(yaw);
            this.setHeadYaw(yaw);

            this.setVelocity(horizontalDirection.multiply(0.48D).add(0.0D, this.getVelocity().y, 0.0D));
            this.velocityModified = true;
        }

        setRushTicks(ticks + 1);
    }

    private void stopRush() {
        setRushTicks(0);
        this.getNavigation().stop();
    }

    private void finishRushWithAttack2() {
        stopRush();
        this.setVelocity(0.0D, this.getVelocity().y, 0.0D);
        this.velocityModified = true;
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
        areaDamage((ServerWorld) getWorld(), 4.0D, damage, 0, 0, true);
    }

    @Override
    public void runSkill_2(PiglinGeneralEntity entity) {
        areaDamage((ServerWorld) getWorld(), 5.0D, 280.0F, 15, ATTACK2_MARK_DURATION, false);
    }

    @Override
    public void runSkill_3(PiglinGeneralEntity entity) {
        LivingEntity target = this.getTarget();
        if (target == null || !(this.getWorld() instanceof ServerWorld world) || !EntityUtil.isValidCombatTarget(this, target)) {
            return;
        }

        markedDamage(world, target, 200.0F, 0, 0);
        target.setVelocity(target.getVelocity().x, 1.45D, target.getVelocity().z);
        target.velocityModified = true;
    }

    @Override
    public void runSkill_3_1(PiglinGeneralEntity entity) {
        LivingEntity target = this.getTarget();
        if (target == null || !(this.getWorld() instanceof ServerWorld world) || !EntityUtil.isValidCombatTarget(this, target)) {
            return;
        }

        markedDamage(world, target, 250.0F, 0, 0);
        target.setVelocity(target.getVelocity().x * 0.25D, -1.65D, target.getVelocity().z * 0.25D);
        target.velocityModified = true;
    }

    @Override
    public void runSkill_3_2(PiglinGeneralEntity entity) {
        LivingEntity target = this.getTarget();
        if (target == null || !(this.getWorld() instanceof ServerWorld world) || !EntityUtil.isValidCombatTarget(this, target)) {
            return;
        }

        teleportToUppercutTarget(world, target);
    }

    @Override
    public void runSkill_4(PiglinGeneralEntity entity) {
        for (LivingEntity living : EntityUtil.getNearbyEntity(this, LivingEntity.class, 20.0D, true, EntityUtil.TeamFilter.ALL)) {
            if (living == this || living.isTeammate(this)) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, ATTACK4_DURATION, 4), this);
            } else {
                CombatEffectUtil.addPigSpiritMark(living, this, 10, ATTACK4_DURATION);
            }
        }
    }

    @Override
    public void runSkill_5(PiglinGeneralEntity entity) {
        if (!(this.getWorld() instanceof ServerWorld world)) {
            return;
        }

        Vec3d center = getSwordEnergyDamageCenter();
        Box box = new Box(center.subtract(4.0D, 4.0D, 4.0D), center.add(4.0D, 4.0D, 4.0D));

        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, box,
                target -> EntityUtil.isValidCombatTarget(this, target) && target.squaredDistanceTo(center) <= 16.0D)) {
            markedDamage(world, target, 320.0F, 50, ATTACK5_MARK_DURATION);
        }
    }

    @Override
    public void runSkill_6(PiglinGeneralEntity entity) {
        if (!(this.getWorld() instanceof ServerWorld world)) {
            return;
        }

        for (LivingEntity target : EntityUtil.getEntitiesInCone(this, LivingEntity.class, 5.0D, 85.0F, EntityUtil.TeamFilter.EXCLUDE_TEAM)) {
            markedDamage(world, target, 300.0F, 0, 0);
        }
    }

    @Override
    public void runSkill_7(PiglinGeneralEntity entity) {
        areaDamage((ServerWorld) getWorld(), 5.5D, 220.0F, 0, 0, false);
    }

    @Override
    public void runSkill_7_1(PiglinGeneralEntity entity) {
        areaDamage((ServerWorld) getWorld(), 5.5D, 300.0F, 0, 0, false);
    }

    private void areaDamage(ServerWorld world, double radius, float damage, int markLayers, int markDurationTicks, boolean armorPiercing) {
        List<LivingEntity> targets = EntityUtil.getNearbyEntity(this, LivingEntity.class, radius, false, EntityUtil.TeamFilter.EXCLUDE_TEAM);

        for (LivingEntity target : targets) {
            if (markedDamage(world, target, damage, markLayers, markDurationTicks) && armorPiercing) {
                CombatEffectUtil.addStackingArmorPiercing(target, this);
            }
        }
    }

    private boolean markedDamage(ServerWorld world, LivingEntity target, float damage, int markLayers, int markDurationTicks) {
        StatusEffectInstance mark = target.getStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
        float multiplier = mark == null ? 1.0F : 1.3F;

        DamageSource source = this.getDamageSources().mobAttack(this);
        boolean hit = target.damage(world, source, damage * multiplier);

        if (hit && markLayers > 0) {
            CombatEffectUtil.addPigSpiritMark(target, this, markLayers, markDurationTicks);
        }

        return hit;
    }

    private void teleportToUppercutTarget(ServerWorld world, LivingEntity target) {
        Vec3d aboveTarget = target.getPos().add(0.0D, target.getHeight() + 0.6D, 0.0D);
        Vec3d fallback = target.getPos();
        Vec3d destination = canStandAt(world, aboveTarget) ? aboveTarget : fallback;
        this.requestTeleport(destination.x, destination.y, destination.z);
    }

    private boolean canStandAt(ServerWorld world, Vec3d pos) {
        Box box = this.getBoundingBox().offset(pos.subtract(this.getPos()));
        return world.isSpaceEmpty(this, box);
    }

    private Vec3d getSwordEnergyDamageCenter() {
        if (this.swordEnergyPos != null && this.age - this.swordEnergyPosAge <= 10) {
            return this.swordEnergyPos;
        }

        Vec3d direction = this.getRotationVec(1.0F).normalize();
        return this.getEyePos().add(direction.multiply(4.0D));
    }

    public void setSwordEnergyPos(Vec3d swordEnergyPos) {
        this.swordEnergyPos = swordEnergyPos;
        this.swordEnergyPosAge = this.age;
    }

    @Override
    public MobEntity getEntity() {
        return this;
    }

    @Override
    public int getSkillCount() {
        return 6;
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
            this.dataTracker.set(SKILL_COOLDOWN_6, getMaxSkillCooldown_6());
            return;
        }

        GeneralEntity.super.setSkillCooldown(skill);
    }

    @Override
    public int getSkillCooldown(String skill) {
        if ("attack7".equals(skill)) {
            return this.dataTracker.get(SKILL_COOLDOWN_6);
        }

        return GeneralEntity.super.getSkillCooldown(skill);
    }

    public int getRushTicks() {
        return this.dataTracker.get(RUSH_TICKS);
    }

    public void setRushTicks(int ticks) {
        this.dataTracker.set(RUSH_TICKS, ticks);
    }

    public boolean isRushing() {
        return getRushTicks() > 0;
    }

    public int getNormalAttackDamage() {
        return this.dataTracker.get(NORMAL_ATTACK_DAMAGE);
    }

    public void setNormalAttackDamage(int damage) {
        this.dataTracker.set(NORMAL_ATTACK_DAMAGE, damage);
    }

    public boolean isPlayingDeathAnimation() {
        return this.dataTracker.get(DYING_ANIMATION);
    }

    public void setPlayingDeathAnimation(boolean dying) {
        this.dataTracker.set(DYING_ANIMATION, dying);
    }

    public int getDeathAnimationTicks() {
        return this.dataTracker.get(DEATH_ANIMATION_TICKS);
    }

    public void setDeathAnimationTicks(int ticks) {
        this.dataTracker.set(DEATH_ANIMATION_TICKS, ticks);
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
        event.renderState().addGeckolibData(PiglinGeneralEntityRenderer.ENTITY_ID, this.getUuid());

        if (this.isDead() || isPlayingDeathAnimation()) {
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
    protected net.minecraft.sound.SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIGLIN_BRUTE_DEATH;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target instanceof AbstractPiglinEntity
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
    public PiglinActivity getActivity() {
        return this.hasSkill() || this.isRushing() ? PiglinActivity.ATTACKING_WITH_MELEE_WEAPON : PiglinActivity.DEFAULT;
    }

    @Override
    protected void playZombificationSound() {
        this.playSound(SoundEvents.ENTITY_PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 30000.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.31D)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0D)
                .add(EntityAttributes.ARMOR, 20.0D)
                .add(EntityAttributes.ARMOR_TOUGHNESS, 12.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 100.0D)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.8D)
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
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            long time = this.mob.getWorld().getTime();

            if (time - this.lastUpdateTime < 20L) {
                return false;
            }

            this.lastUpdateTime = time;

            LivingEntity target = this.mob.getTarget();
            if (!this.isValidTarget(target)) {
                return false;
            }

            this.path = this.mob.getNavigation().findPathTo(target, 0);
            return this.path != null || this.mob.isInAttackRange(target) || this.mob.distanceTo(target) <= 20.0D;
        }

        @Override
        public boolean shouldContinue() {
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
                return !this.mob.getNavigation().isIdle();
            }

            if (!this.mob.isInPositionTargetRange(target.getBlockPos())) {
                return false;
            }

            return !(target instanceof PlayerEntity player && (player.isSpectator() || player.isCreative()));
        }

        @Override
        public void start() {
            if (this.path != null) {
                this.mob.getNavigation().startMovingAlong(this.path, this.speed);
            }

            this.mob.setAttacking(true);
            this.updateCountdownTicks = 0;
            this.cooldown = 0;
        }

        @Override
        public void stop() {
            LivingEntity target = this.mob.getTarget();

            if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(target)) {
                this.mob.setTarget(null);
            }

            this.mob.setAttacking(false);

            if (!this.mob.isRushing()) {
                this.mob.getNavigation().stop();
            }
        }

        @Override
        public boolean shouldRunEveryTick() {
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

            this.mob.getLookControl().lookAt(target, 30.0F, 30.0F);

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

            if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(target))
                    && this.updateCountdownTicks <= 0
                    && (
                    this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D
                            || target.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0D
                            || this.mob.getRandom().nextFloat() < 0.05F
            )) {
                this.targetX = target.getX();
                this.targetY = target.getY();
                this.targetZ = target.getZ();

                this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);

                double squaredDistance = this.mob.squaredDistanceTo(target);
                if (squaredDistance > 1024.0D) {
                    this.updateCountdownTicks += 10;
                } else if (squaredDistance > 256.0D) {
                    this.updateCountdownTicks += 5;
                }

                if (!this.mob.getNavigation().startMovingTo(target, this.speed)) {
                    this.updateCountdownTicks += 15;
                }

                this.updateCountdownTicks = this.getTickCount(this.updateCountdownTicks);
            }

            this.cooldown = Math.max(this.cooldown - 1, 0);
            this.attack(target);
        }

        private void attack(LivingEntity target) {
            if (this.canAttack(target)) {
                this.resetCooldown();
                this.mob.swingHand(Hand.MAIN_HAND);
                this.mob.tryAttack((ServerWorld) this.mob.getWorld(), target);
            }
        }

        private void resetCooldown() {
            this.cooldown = this.getTickCount(20);
        }

        private boolean isCooledDown() {
            return this.cooldown <= 0;
        }

        private boolean canAttack(LivingEntity target) {
            return this.isCooledDown()
                    && this.mob.isInAttackRange(target)
                    && this.mob.getVisibilityCache().canSee(target);
        }

        private boolean isValidTarget(@Nullable LivingEntity target) {
            return target != null
                    && target.isAlive()
                    && !(target instanceof AbstractPiglinEntity)
                    && EntityUtil.isValidCombatTarget(this.mob, target);
        }
    }
}