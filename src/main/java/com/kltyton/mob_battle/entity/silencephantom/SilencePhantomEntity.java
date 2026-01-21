package com.kltyton.mob_battle.entity.silencephantom;

import com.kltyton.mob_battle.entity.general.GeneralEntityOnlyOneSkill;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;

public class SilencePhantomEntity extends PhantomEntity implements GeoEntity, GeneralEntityOnlyOneSkill<SilencePhantomEntity> {
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(SilencePhantomEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public SilencePhantomEntity(EntityType<? extends PhantomEntity> entityType, World world) {
        super(entityType, world);
        this.setAiDisabled(false);
        this.setHasSkill(false);
    }
    @Override
    public boolean hasSkill() {
        return getDataTracker().get(HAS_SKILL);
    }
    @Override
    public void setHasSkill(boolean hasSkill) {
        getDataTracker().set(HAS_SKILL, hasSkill);
    }

    public boolean canSkill() {
        return !this.getWorld().isClient() && !hasSkill() && this.getTarget() != null;
    }
    public void performSkill() {
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.movementType = PhantomMovementType.CIRCLE;
        this.triggerAnim("attack_controller", "attack");
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            if (!hasSkill()) {
                this.setAiDisabled(false);
            }
        }
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>("main_controller", 5 ,this::animationController));
        controllerRegistrar.add(new AnimationController<>( "attack_controller",animTest -> {
                    if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED && this.hasSkill()) {
                        ClientPlayNetworking.send(new SkillPayload(
                                "stop", this.getId()
                        ));
                    }
                    return PlayState.STOP;
                })
                        .triggerableAnim("attack", ATTACK_ANIM)
                        .setCustomInstructionKeyframeHandler(s -> {
                            PlayerEntity player = ClientUtil.getClientPlayer();
                            if ("runAttack;".equals(s.keyframeData().getInstructions())) {
                                player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "attack", this.getId()
                                ));
                            }
                        })
        );
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    private PlayState animationController(final AnimationTest<SilencePhantomEntity> state) {
        if (state.isMoving()) {
            return state.setAndContinue(WALK_ANIM);
        } else {
            return state.setAndContinue(IDEA_ANIM);
        }
    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SilencePhantomEntity.StartAttackGoal());
        this.goalSelector.add(3, new SilencePhantomEntity.CircleMovementGoal());
        this.targetSelector.add(1, new SilencePhantomEntity.FindTargetGoal());
    }
    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isAiDisabled()) {
            super.takeKnockback(strength, x, z);
        }
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void runSkill(SilencePhantomEntity entity) {
        LivingEntity target = entity.getTarget();
        if (target == null || entity.getWorld().isClient) return;
        World world = entity.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return;
        Vec3d startPos = entity.getEyePos();
        Vec3d targetPos = target.getEyePos();
        Vec3d direction = targetPos.subtract(startPos).normalize();
        world.playSound(null, entity.getBlockPos(),
                SoundEvents.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.HOSTILE, 3.0f, 1.0f);
        double distance = startPos.distanceTo(targetPos);
        for (int i = 0; i < (int) distance; i++) {
            Vec3d particlePos = startPos.add(direction.multiply(i));
            serverWorld.spawnParticles(
                    ParticleTypes.SONIC_BOOM,
                    particlePos.x, particlePos.y, particlePos.z,
                    1, 0.0, 0.0, 0.0, 0.0
            );
        }

        if (distance <= 120.0) {
            target.takeKnockback(1.5, -direction.x, -direction.z);
            target.damage(serverWorld,
                    world.getDamageSources().sonicBoom(entity),
                    20.0f // 伤害数值
            );
            world.playSound(null, target.getBlockPos(),
                    SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.HOSTILE, 3.0f, 1.0f);
        }
    }
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 130.0D);
    }
    public class StartAttackGoal extends Goal {
        private int cooldown;

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = SilencePhantomEntity.this.getTarget();
            return livingEntity != null && SilencePhantomEntity.this.testTargetPredicate(castToServerWorld(SilencePhantomEntity.this.getWorld()), livingEntity, TargetPredicate.DEFAULT);
        }

        @Override
        public void start() {
            this.cooldown = this.getTickCount(10);
            SilencePhantomEntity.this.movementType = SilencePhantomEntity.PhantomMovementType.CIRCLE;
            this.startSwoop();
        }

        @Override
        public void stop() {
            if (SilencePhantomEntity.this.circlingCenter != null) {
                SilencePhantomEntity.this.circlingCenter = SilencePhantomEntity.this.getWorld()
                        .getTopPosition(Heightmap.Type.MOTION_BLOCKING, SilencePhantomEntity.this.circlingCenter)
                        .up(10 + SilencePhantomEntity.this.random.nextInt(20));
            }
        }

        @Override
        public void tick() {
            if (SilencePhantomEntity.this.movementType == SilencePhantomEntity.PhantomMovementType.CIRCLE) {
                this.cooldown--;
                if (this.cooldown <= 0) {
                    SilencePhantomEntity.this.movementType = SilencePhantomEntity.PhantomMovementType.SWOOP;
                    this.startSwoop();
                    this.cooldown = this.getTickCount((8 + SilencePhantomEntity.this.random.nextInt(4)) * 20);
                    SilencePhantomEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0F, 0.95F + SilencePhantomEntity.this.random.nextFloat() * 0.1F);
                }
            }
        }
        private void startSwoop() {
            performSkill();
        }
    }
    public class FindTargetGoal extends Goal {
        private final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(64.0F);
        private int delay = toGoalTicks(20);

        FindTargetGoal() {
        }

        public boolean canStart() {
            if (this.delay > 0) {
                --this.delay;
            } else {
                this.delay = toGoalTicks(60);
                ServerWorld serverWorld = castToServerWorld(SilencePhantomEntity.this.getWorld());
                List<PlayerEntity> list = serverWorld.getPlayers(this.PLAYERS_IN_RANGE_PREDICATE, SilencePhantomEntity.this, SilencePhantomEntity.this.getBoundingBox().expand(16.0F, 64.0F, 16.0F));
                if (!list.isEmpty()) {
                    list.sort(Comparator.comparing(Entity::getY).reversed());

                    for(PlayerEntity playerEntity : list) {
                        if (SilencePhantomEntity.this.testTargetPredicate(serverWorld, playerEntity, TargetPredicate.DEFAULT)) {
                            SilencePhantomEntity.this.setTarget(playerEntity);
                            return true;
                        }
                    }
                }

            }
            return false;
        }

        public boolean shouldContinue() {
            LivingEntity livingEntity = SilencePhantomEntity.this.getTarget();
            return livingEntity != null && SilencePhantomEntity.this.testTargetPredicate(castToServerWorld(SilencePhantomEntity.this.getWorld()), livingEntity, TargetPredicate.DEFAULT);
        }
    }
    public class CircleMovementGoal extends MovementGoal {
        private float angle;
        private float radius;
        private float yOffset;
        private float circlingDirection;

        CircleMovementGoal() {
        }

        public boolean canStart() {
            return SilencePhantomEntity.this.getTarget() == null || SilencePhantomEntity.this.movementType == SilencePhantomEntity.PhantomMovementType.CIRCLE;
        }

        public void start() {
            this.radius = 5.0F + SilencePhantomEntity.this.random.nextFloat() * 10.0F;
            this.yOffset = -4.0F + SilencePhantomEntity.this.random.nextFloat() * 9.0F;
            this.circlingDirection = SilencePhantomEntity.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.adjustDirection();
        }

        public void tick() {
            if (SilencePhantomEntity.this.random.nextInt(this.getTickCount(350)) == 0) {
                this.yOffset = -4.0F + SilencePhantomEntity.this.random.nextFloat() * 9.0F;
            }

            if (SilencePhantomEntity.this.random.nextInt(this.getTickCount(250)) == 0) {
                ++this.radius;
                if (this.radius > 15.0F) {
                    this.radius = 5.0F;
                    this.circlingDirection = -this.circlingDirection;
                }
            }

            if (SilencePhantomEntity.this.random.nextInt(this.getTickCount(450)) == 0) {
                this.angle = SilencePhantomEntity.this.random.nextFloat() * 2.0F * (float)Math.PI;
                this.adjustDirection();
            }

            if (this.isNearTarget()) {
                this.adjustDirection();
            }

            if (SilencePhantomEntity.this.targetPosition.y < SilencePhantomEntity.this.getY() && !SilencePhantomEntity.this.getWorld().isAir(SilencePhantomEntity.this.getBlockPos().down(1))) {
                this.yOffset = Math.max(1.0F, this.yOffset);
                this.adjustDirection();
            }

            if (SilencePhantomEntity.this.targetPosition.y > SilencePhantomEntity.this.getY() && !SilencePhantomEntity.this.getWorld().isAir(SilencePhantomEntity.this.getBlockPos().up(1))) {
                this.yOffset = Math.min(-1.0F, this.yOffset);
                this.adjustDirection();
            }

        }

        private void adjustDirection() {
            if (SilencePhantomEntity.this.circlingCenter == null) {
                SilencePhantomEntity.this.circlingCenter = SilencePhantomEntity.this.getBlockPos();
            }

            this.angle += this.circlingDirection * 15.0F * ((float)Math.PI / 180F);
            SilencePhantomEntity.this.targetPosition = Vec3d.of(SilencePhantomEntity.this.circlingCenter).add(this.radius * MathHelper.cos(this.angle), -4.0F + this.yOffset, this.radius * MathHelper.sin(this.angle));
        }
    }

}
