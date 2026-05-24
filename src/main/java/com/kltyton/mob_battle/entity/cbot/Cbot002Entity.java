package com.kltyton.mob_battle.entity.cbot;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.general.GeneralEntity;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Cbot002Entity extends HostileEntity implements GeneralEntity<Cbot002Entity> {
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(Cbot002Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN_1 = DataTracker.registerData(Cbot002Entity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_2 = DataTracker.registerData(Cbot002Entity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_3 = DataTracker.registerData(Cbot002Entity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_4 = DataTracker.registerData(Cbot002Entity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SKILL_COOLDOWN_5 = DataTracker.registerData(Cbot002Entity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SHOOT_MODE = DataTracker.registerData(Cbot002Entity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SHOOT_TICKS = DataTracker.registerData(Cbot002Entity.class, TrackedDataHandlerRegistry.INTEGER);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public Cbot002Entity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 30;
        this.setHasSkill(false);
        this.setAiDisabled(false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(8, new LookAtEntityGoal(this, LivingEntity.class, 8.0F));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (entity, world) -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(this, living)));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        entityInitDataTracker(builder);
        builder.add(SHOOT_MODE, 0);
        builder.add(SHOOT_TICKS, 0);
    }

    @Override
    public void tick() {
        super.tick();
        entityTick();
        if (!this.getWorld().isClient()) {
            if (this.age % 20 == 0) {
                this.heal(2.0F);
            }
            tickCombat((ServerWorld) this.getWorld());
            tickShooting((ServerWorld) this.getWorld());
        }
    }

    private void tickCombat(ServerWorld world) {
        if (this.hasSkill() || getShootMode() != 0) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !EntityUtil.isValidCombatTarget(this, target)) {
            return;
        }
        this.getLookControl().lookAt(target, 30.0F, 30.0F);
        double distance = this.distanceTo(target);
        if (distance > 19.5D) {
            this.getNavigation().startMovingTo(target, 1.0D);
            return;
        }
        this.getNavigation().stop();
        this.tryAttack(world, target);
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
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
        if (target == null || !(this.getWorld() instanceof ServerWorld world)) {
            return;
        }
        throwIceBlock(world, target);
    }

    @Override
    public void runSkill_4(Cbot002Entity entity) {
        setShootMode(2);
        setShootTicks(0);
        setAiDisabled(false);
    }

    @Override
    public void stopSkill_4(Cbot002Entity entity) {
        setShootMode(0);
        setAiDisabled(true);
        this.getNavigation().stop();
    }

    private void tickShooting(ServerWorld world) {
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
        Vec3d direction = this.getPos().subtract(target.getPos()).normalize();
        if (distance < 9.0D) {
            this.setVelocity(direction.multiply(0.28D).add(0.0D, this.getVelocity().y, 0.0D));
            this.velocityModified = true;
        } else if (distance > 14.0D) {
            this.getNavigation().startMovingTo(target, 0.8D);
        } else {
            this.getNavigation().stop();
        }
        this.getLookControl().lookAt(target, 30.0F, 30.0F);
    }

    private void shootSnowballSpread(ServerWorld world, LivingEntity target, int count, float damage, float magicDamage, float speed) {
        for (int i = 0; i < count; i++) {
            CbotSnowballEntity snowball = ModEntities.CBOT_SNOWBALL.create(world, SpawnReason.MOB_SUMMONED);
            if (snowball == null) {
                continue;
            }
            Vec3d toTarget = target.getEyePos().subtract(this.getEyePos()).add(
                    (this.random.nextDouble() - 0.5D) * 3.4D,
                    (this.random.nextDouble() - 0.5D) * 1.8D,
                    (this.random.nextDouble() - 0.5D) * 3.4D
            ).normalize().multiply(speed);
            snowball.configure(this, this.getEyePos().add(this.getRotationVec(1.0F).multiply(0.8D)), toTarget, damage, magicDamage);
            world.spawnEntity(snowball);
        }
        this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 0.8F, 0.75F + this.random.nextFloat() * 0.25F);
    }

    private void throwIceBlock(ServerWorld world, LivingEntity target) {
        Vec3d start = this.getEyePos().add(this.getRotationVec(1.0F).multiply(1.2D));
        SnowmanIceBlockEntity ice = ModEntities.SNOWMAN_ICE_BLOCK.create(world, SpawnReason.MOB_SUMMONED);
        if (ice == null) {
            return;
        }
        ice.setOwner(this);
        ice.setPosition(start);
        Vec3d velocity = target.getEyePos().subtract(start).normalize().multiply(0.75D);
        ice.setVelocity(velocity);
        ice.velocityModified = true;
        world.spawnEntity(ice);
        world.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_SNOWBALL_THROW, this.getSoundCategory(), 1.0F, 0.7F);
    }

    @Override
    public MobEntity getEntity() {
        return this;
    }

    @Override
    public int getSkillCount() {
        return 3;
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
        return this.dataTracker.get(SHOOT_MODE);
    }

    public void setShootMode(int mode) {
        this.dataTracker.set(SHOOT_MODE, mode);
    }

    public int getShootTicks() {
        return this.dataTracker.get(SHOOT_TICKS);
    }

    public void setShootTicks(int ticks) {
        this.dataTracker.set(SHOOT_TICKS, ticks);
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

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 1200.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.28D)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0D)
                .add(EntityAttributes.STEP_HEIGHT, 2.0D)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.60D);
    }
}
