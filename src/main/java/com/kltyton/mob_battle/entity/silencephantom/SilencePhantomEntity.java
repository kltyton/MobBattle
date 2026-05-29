package com.kltyton.mob_battle.entity.silencephantom;

import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.general.GeneralEntityOnlyOneSkill;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.tags.ModTags;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
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

public class SilencePhantomEntity extends Phantom implements  GeneralEntityOnlyOneSkill<SilencePhantomEntity> {
    public static final EntityDataAccessor<Boolean> HAS_SKILL = SynchedEntityData.defineId(SilencePhantomEntity.class, EntityDataSerializers.BOOLEAN);
    public SilencePhantomEntity(EntityType<? extends Phantom> entityType, Level world) {
        super(entityType, world);
        this.setNoAi(false);
        this.setHasSkill(false);
    }
    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if (!ModSkillEntityType.canSkill(this)) return false;
        return super.doHurtTarget(world, target);
    }
    @Override
    public boolean hasSkill() {
        return getEntityData().get(HAS_SKILL);
    }
    @Override
    public void setHasSkill(boolean hasSkill) {
        getEntityData().set(HAS_SKILL, hasSkill);
    }

    @Override
    public boolean canSkill() {
        if (!ModSkillEntityType.canSkill(this)) return false;
        return !this.level().isClientSide() && !hasSkill() && this.getTarget() != null;
    }
    public void performSkill() {
        this.setHasSkill(true);
        this.setNoAi(true);
        this.attackPhase = Phantom.AttackPhase.CIRCLE;
        this.triggerAnim("attack_controller", "attack");
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (!hasSkill()) {
                this.setNoAi(false);
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
                            Player player = ClientUtil.getClientPlayer();
                            if ("runAttack;".equals(s.keyframeData().getInstructions())) {
                                player.playSound(SoundEvents.ANVIL_LAND, 1.0F, 1.0F);
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SilencePhantomEntity.StartAttackGoal());
        this.goalSelector.addGoal(3, new SilencePhantomEntity.CircleMovementGoal());
        this.targetSelector.addGoal(1, new SilencePhantomEntity.FindTargetGoal());
    }
    @Override
    public void knockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isNoAi()) {
            super.knockback(strength, x, z);
        }
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void runSkill(SilencePhantomEntity entity) {
        LivingEntity target = entity.getTarget();
        if (target == null || entity.level().isClientSide) return;
        Level world = entity.level();
        if (!(world instanceof ServerLevel serverWorld)) return;
        Vec3 startPos = entity.getEyePosition();
        Vec3 targetPos = target.getEyePosition();
        Vec3 direction = targetPos.subtract(startPos).normalize();
        world.playSound(null, entity.blockPosition(),
                SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 3.0f, 1.0f);
        double distance = startPos.distanceTo(targetPos);
        for (int i = 0; i < (int) distance; i++) {
            Vec3 particlePos = startPos.add(direction.scale(i));
            serverWorld.sendParticles(
                    ParticleTypes.SONIC_BOOM,
                    particlePos.x, particlePos.y, particlePos.z,
                    1, 0.0, 0.0, 0.0, 0.0
            );
        }

        if (distance <= 120.0) {
            target.knockback(1.5, -direction.x, -direction.z);
            target.hurtServer(serverWorld,
                    world.damageSources().sonicBoom(entity),
                    20.0f // 伤害数值
            );
            world.playSound(null, target.blockPosition(),
                    SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 3.0f, 1.0f);
        }
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 130.0D);
    }
    public class StartAttackGoal extends Goal {
        private int cooldown;

        @Override
        public boolean canUse() {
            LivingEntity livingEntity = SilencePhantomEntity.this.getTarget();
            return livingEntity != null && SilencePhantomEntity.this.canAttack(getServerLevel(SilencePhantomEntity.this.level()), livingEntity, TargetingConditions.DEFAULT);
        }

        @Override
        public void start() {
            this.cooldown = this.adjustedTickDelay(10);
            SilencePhantomEntity.this.attackPhase = Phantom.AttackPhase.CIRCLE;
            this.startSwoop();
        }

        @Override
        public void stop() {
            if (SilencePhantomEntity.this.anchorPoint != null) {
                SilencePhantomEntity.this.anchorPoint = SilencePhantomEntity.this.level()
                        .getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, SilencePhantomEntity.this.anchorPoint)
                        .above(10 + SilencePhantomEntity.this.random.nextInt(20));
            }
        }

        @Override
        public void tick() {
            if (SilencePhantomEntity.this.attackPhase == Phantom.AttackPhase.CIRCLE) {
                this.cooldown--;
                if (this.cooldown <= 0) {
                    SilencePhantomEntity.this.attackPhase = Phantom.AttackPhase.SWOOP;
                    this.startSwoop();
                    this.cooldown = this.adjustedTickDelay((8 + SilencePhantomEntity.this.random.nextInt(4)) * 20);
                    SilencePhantomEntity.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + SilencePhantomEntity.this.random.nextFloat() * 0.1F);
                }
            }
        }

        private void startSwoop() {
            performSkill();
        }
    }

    public class FindTargetGoal extends Goal {
        private final TargetingConditions PLAYERS_IN_RANGE_PREDICATE = TargetingConditions.forCombat().range(64.0)
                .selector((entity, world) -> !entity.getType().is(ModTags.SILENCE_PHANTOM_CANNOT_ATTACK));
        private int delay = reducedTickDelay(20);

        FindTargetGoal() {
        }

        @Override
        public boolean canUse() {
            if (this.delay > 0) {
                --this.delay;
            } else {
                this.delay = reducedTickDelay(60);
                ServerLevel serverWorld = getServerLevel(SilencePhantomEntity.this.level());
                List<LivingEntity> list = serverWorld.getNearbyEntities(LivingEntity.class, this.PLAYERS_IN_RANGE_PREDICATE, SilencePhantomEntity.this, SilencePhantomEntity.this.getBoundingBox().inflate(16.0F, 64.0F, 16.0F));
                if (!list.isEmpty()) {
                    list.sort(Comparator.<LivingEntity, Double>comparing(Entity::getY).reversed());
                    for (LivingEntity playerEntity : list) {
                        if (SilencePhantomEntity.this.canAttack(serverWorld, playerEntity, TargetingConditions.DEFAULT)) {
                            SilencePhantomEntity.this.setTarget(playerEntity);
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingEntity = SilencePhantomEntity.this.getTarget();
            return livingEntity != null && SilencePhantomEntity.this.canAttack(getServerLevel(SilencePhantomEntity.this.level()), livingEntity, TargetingConditions.DEFAULT);
        }
    }

    public class CircleMovementGoal extends PhantomMoveTargetGoal {
        private float angle;
        private float radius;
        private float yOffset;
        private float circlingDirection;

        CircleMovementGoal() {
        }

        @Override
        public boolean canUse() {
            return SilencePhantomEntity.this.getTarget() == null || SilencePhantomEntity.this.attackPhase == Phantom.AttackPhase.CIRCLE;
        }

        @Override
        public void start() {
            this.radius = 5.0F + SilencePhantomEntity.this.random.nextFloat() * 10.0F;
            this.yOffset = -4.0F + SilencePhantomEntity.this.random.nextFloat() * 9.0F;
            this.circlingDirection = SilencePhantomEntity.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.adjustDirection();
        }

        @Override
        public void tick() {
            if (SilencePhantomEntity.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
                this.yOffset = -4.0F + SilencePhantomEntity.this.random.nextFloat() * 9.0F;
            }

            if (SilencePhantomEntity.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
                ++this.radius;
                if (this.radius > 15.0F) {
                    this.radius = 5.0F;
                    this.circlingDirection = -this.circlingDirection;
                }
            }

            if (SilencePhantomEntity.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = SilencePhantomEntity.this.random.nextFloat() * 2.0F * (float)Math.PI;
                this.adjustDirection();
            }

            if (this.touchingTarget()) {
                this.adjustDirection();
            }

            if (SilencePhantomEntity.this.moveTargetPoint.y < SilencePhantomEntity.this.getY() && !SilencePhantomEntity.this.level().isEmptyBlock(SilencePhantomEntity.this.blockPosition().below(1))) {
                this.yOffset = Math.max(1.0F, this.yOffset);
                this.adjustDirection();
            }

            if (SilencePhantomEntity.this.moveTargetPoint.y > SilencePhantomEntity.this.getY() && !SilencePhantomEntity.this.level().isEmptyBlock(SilencePhantomEntity.this.blockPosition().above(1))) {
                this.yOffset = Math.min(-1.0F, this.yOffset);
                this.adjustDirection();
            }

        }

        private void adjustDirection() {
            if (SilencePhantomEntity.this.anchorPoint == null) {
                SilencePhantomEntity.this.anchorPoint = SilencePhantomEntity.this.blockPosition();
            }

            this.angle += this.circlingDirection * 15.0F * ((float)Math.PI / 180F);
            SilencePhantomEntity.this.moveTargetPoint = Vec3.atLowerCornerOf(SilencePhantomEntity.this.anchorPoint).add(this.radius * Mth.cos(this.angle), -4.0F + this.yOffset, this.radius * Mth.sin(this.angle));
        }
    }
}
