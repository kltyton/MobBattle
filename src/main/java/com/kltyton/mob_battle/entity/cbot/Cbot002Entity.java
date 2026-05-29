package com.kltyton.mob_battle.entity.cbot;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.general.GeneralEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Cbot002Entity extends Monster implements GeneralEntity<Cbot002Entity> {
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(Cbot002Entity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_1 = SynchedEntityData.defineId(Cbot002Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_2 = SynchedEntityData.defineId(Cbot002Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_3 = SynchedEntityData.defineId(Cbot002Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_4 = SynchedEntityData.defineId(Cbot002Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN_5 = SynchedEntityData.defineId(Cbot002Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SHOOT_MODE = SynchedEntityData.defineId(Cbot002Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SHOOT_TICKS = SynchedEntityData.defineId(Cbot002Entity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public Cbot002Entity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.xpReward = 30;
        this.setHasSkill(false);
        this.setNoAi(false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, LivingEntity.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (entity, world) -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(this, living)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        entityInitDataTracker(builder);
        builder.define(SHOOT_MODE, 0);
        builder.define(SHOOT_TICKS, 0);
    }

    @Override
    public void tick() {
        super.tick();
        entityTick();
        if (!this.level().isClientSide()) {
            if (this.tickCount % 20 == 0) {
                this.heal(2.0F);
            }
            tickCombat((ServerLevel) this.level());
            tickShooting((ServerLevel) this.level());
        }
    }

    private void tickCombat(ServerLevel world) {
        if (this.hasSkill() || getShootMode() != 0) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !EntityUtil.isValidCombatTarget(this, target)) {
            return;
        }
        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double distance = this.distanceTo(target);
        if (distance > 19.5D) {
            this.getNavigation().moveTo(target, 1.0D);
            return;
        }
        this.getNavigation().stop();
        this.doHurtTarget(world, target);
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!(target instanceof LivingEntity living) || !EntityUtil.isValidCombatTarget(this, living) || hasSkill()) {
            return false;
        }
        if (canSkill("attack4") && this.distanceTo(living) <= 18.0D) {
            performSkill("attack4");
            return true;
        }
        if (canSkill("attack3") && this.distanceTo(living) <= 20.0D) {
            performSkill("attack3");
            return true;
        }
        if (canSkill("attack2")) {
            performSkill("attack2");
            return true;
        }
        return true;
    }

    @Override
    public void runSkill_2(Cbot002Entity entity) {
        setShootMode(1);
        setShootTicks(0);
    }

    @Override
    public void stopSkill_2(Cbot002Entity entity) {
        setShootMode(0);
    }

    @Override
    public void runSkill_3(Cbot002Entity entity) {
        LivingEntity target = this.getTarget();
        if (target == null || !(this.level() instanceof ServerLevel world)) {
            return;
        }
        throwIceBlock(world, target);
    }

    @Override
    public void runSkill_4(Cbot002Entity entity) {
        setShootMode(2);
        setShootTicks(0);
        setNoAi(false);
    }

    @Override
    public void stopSkill_4(Cbot002Entity entity) {
        setShootMode(0);
        setNoAi(true);
        this.getNavigation().stop();
    }

    private void tickShooting(ServerLevel world) {
        int mode = getShootMode();
        if (mode == 0) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !EntityUtil.isValidCombatTarget(this, target)) {
            return;
        }
        if (mode == 2) {
            keepDistanceFrom(target);
        }
        int ticks = getShootTicks() + 1;
        setShootTicks(ticks);
        if (ticks % 4 == 0) {
            if (mode == 1) {
                shootSnowballSpread(world, target, 2, 30.0F, 0.0F, 1.35F);
            } else {
                shootSnowballSpread(world, target, 4, 40.0F, 5.0F, 1.45F);
            }
        }
    }

    private void keepDistanceFrom(LivingEntity target) {
        double distance = this.distanceTo(target);
        Vec3 direction = this.position().subtract(target.position()).normalize();
        if (distance < 9.0D) {
            this.setDeltaMovement(direction.scale(0.28D).add(0.0D, this.getDeltaMovement().y, 0.0D));
            this.hurtMarked = true;
        } else if (distance > 14.0D) {
            this.getNavigation().moveTo(target, 0.8D);
        } else {
            this.getNavigation().stop();
        }
        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
    }

    private void shootSnowballSpread(ServerLevel world, LivingEntity target, int count, float damage, float magicDamage, float speed) {
        for (int i = 0; i < count; i++) {
            CbotSnowballEntity snowball = ModEntities.CBOT_SNOWBALL.create(world, EntitySpawnReason.MOB_SUMMONED);
            if (snowball == null) {
                continue;
            }
            Vec3 toTarget = target.getEyePosition().subtract(this.getEyePosition()).add(
                    (this.random.nextDouble() - 0.5D) * 3.4D,
                    (this.random.nextDouble() - 0.5D) * 1.8D,
                    (this.random.nextDouble() - 0.5D) * 3.4D
            ).normalize().scale(speed);
            snowball.configure(this, this.getEyePosition().add(this.getViewVector(1.0F).scale(0.8D)), toTarget, damage, magicDamage);
            world.addFreshEntity(snowball);
        }
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 0.8F, 0.75F + this.random.nextFloat() * 0.25F);
    }

    private void throwIceBlock(ServerLevel world, LivingEntity target) {
        Vec3 start = this.getEyePosition().add(this.getViewVector(1.0F).scale(1.2D));
        SnowmanIceBlockEntity ice = ModEntities.SNOWMAN_ICE_BLOCK.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (ice == null) {
            return;
        }
        ice.setOwner(this);
        ice.setPos(start);
        Vec3 velocity = target.getEyePosition().subtract(start).normalize().scale(0.75D);
        ice.setDeltaMovement(velocity);
        ice.hurtMarked = true;
        world.addFreshEntity(ice);
        world.playSound(null, this.blockPosition(), SoundEvents.SNOWBALL_THROW, this.getSoundSource(), 1.0F, 0.7F);
    }

    @Override
    public Mob getEntity() {
        return this;
    }

    @Override
    public int getSkillCount() {
        return 3;
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
        return 40;
    }

    @Override
    public int getMaxSkillCooldown_2() {
        return 18 * 20;
    }

    @Override
    public int getMaxSkillCooldown_3() {
        return 20 * 20;
    }

    public int getShootMode() {
        return this.entityData.get(SHOOT_MODE);
    }

    public void setShootMode(int mode) {
        this.entityData.set(SHOOT_MODE, mode);
    }

    public int getShootTicks() {
        return this.entityData.get(SHOOT_TICKS);
    }

    public void setShootTicks(int ticks) {
        this.entityData.set(SHOOT_TICKS, ticks);
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
                .triggerableAnim("attack2", ATTACK_ANIM_2)
                .triggerableAnim("attack3", ATTACK_ANIM_3)
                .triggerableAnim("attack4", ATTACK_ANIM_4)
                .setCustomInstructionKeyframeHandler(s -> {
                    String instruction = s.keyframeData().getInstructions().replaceAll("\\s+", "");
                    if ("runAttack1_1;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("attack2", this.getId()));
                    } else if ("runAttack1_2;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("attack2_stop", this.getId()));
                    } else if ("runAttack3;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("attack3", this.getId()));
                    } else if ("runAttack4_1;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("attack4", this.getId()));
                    } else if ("runAttack4_2;".equals(instruction)) {
                        ClientPlayNetworking.send(new SkillPayload("attack4_stop", this.getId()));
                    }
                }));
    }

    @Override
    public PlayState mainController(AnimationTest<?> state) {
        if (this.hasSkill()) {
            return PlayState.CONTINUE;
        }
        return state.isMoving() ? state.setAndContinue(WALK_ANIM) : state.setAndContinue(IDLE_ANIM);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1200.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.STEP_HEIGHT, 2.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.60D);
    }
}
