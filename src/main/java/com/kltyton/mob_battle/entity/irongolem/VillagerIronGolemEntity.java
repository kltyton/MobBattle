package com.kltyton.mob_battle.entity.irongolem;

import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
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

public class VillagerIronGolemEntity extends IronGolemEntity implements GeoEntity, ModBaseIronGolemEntity {
    public VillagerIronGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
        this.setAiDisabled(false);
        this.setHasSkill(false);
        this.setSkillCooldown(200);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.9, 32.0F));
        this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6, false));
        this.goalSelector.add(4, new IronGolemWanderAroundGoal(this, 0.6));
        this.goalSelector.add(5, new IronGolemLookGoal(this));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackIronGolemTargetGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false, (entity, world) -> entity instanceof Monster));
        this.targetSelector.add(4, new UniversalAngerGoal<>(this, false));
    }
    //skill
    public static final TrackedData<Boolean> HAS_SKILL = DataTracker.registerData(VillagerIronGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN = DataTracker.registerData(VillagerIronGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
        builder.add(SKILL_COOLDOWN, 400);
    }
    public boolean hasSkill() {
        return getDataTracker().get(HAS_SKILL);
    }
    public int getSkillCooldown() {
        return getDataTracker().get(SKILL_COOLDOWN);
    }

    public void setHasSkill(boolean hasSkill) {
        getDataTracker().set(HAS_SKILL, hasSkill);
    }
    public void setSkillCooldown(int cooldown) {
        getDataTracker().set(SKILL_COOLDOWN, cooldown);
    }

    public boolean canSkill() {
        return !this.getWorld().isClient() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }
    public void performSkill() {
        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.setSkillCooldown(200);
        this.triggerAnim("attack_controller", "attack2");
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            if (!hasSkill()) {
                this.setAiDisabled(false);
                // 冷却递减
                int cd = getSkillCooldown();
                if (cd > 0) setSkillCooldown(cd - 1);
            }
        }
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation ATTACK_ANIM_2 = RawAnimation.begin().thenPlay("attack2");
    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isAiDisabled()) {
            super.takeKnockback(strength, x, z);
        }
    }
    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        if (canSkill()) {
            performSkill();
            return true;
        }
        if (this.hasSkill()) return false;
        this.attackTicksLeft = 10;
        world.sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
        float f = this.getAttackDamage();
        float g = (int)f > 0 ? f / 2.0F + this.random.nextInt((int)f) : f;
        DamageSource damageSource = this.getDamageSources().mobAttack(this);
        boolean bl = target.damage(world, damageSource, g);
        if (!this.hasSkill()) {
            this.triggerAnim("attack_controller", "attack");
            this.setHasSkill(true);
        }
        if (bl) {
            double d = target instanceof LivingEntity livingEntity ? livingEntity.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE) : 0.0;
            double e = Math.max(0.0, 1.0 - d);
            target.setVelocity(target.getVelocity().add(0.0, 0.4F * e, 0.0));
            EnchantmentHelper.onTargetDamaged(world, target, damageSource);
        }

        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return bl;
    }
    public boolean tryAttackBase(ServerWorld world, Entity target, float i) {
        this.attackTicksLeft = 10;
        world.sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
        float f = this.getAttackDamage();
        float g = (int)f > 0 ? f / 2.0F + this.random.nextInt((int)f) : f;
        DamageSource damageSource = this.getDamageSources().mobAttack(this);
        boolean bl = target.damage(world, damageSource, g * i);
        if (bl) {
            double d = target instanceof LivingEntity livingEntity ? livingEntity.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE) : 0.0;
            double e = Math.max(0.0, 1.0 - d);
            target.setVelocity(target.getVelocity().add(0.0, 0.4F * e, 0.0));
            EnchantmentHelper.onTargetDamaged(world, target, damageSource);
        }

        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return bl;
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
                .triggerableAnim("attack2", ATTACK_ANIM_2)
                .setSoundKeyframeHandler(s -> {})
                        .setCustomInstructionKeyframeHandler(s -> {
                            PlayerEntity player = ClientUtil.getClientPlayer();
                            if ("damage_1_5".equals(s.keyframeData().getInstructions())) {
                                player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "damage_1_5", this.getId()
                                ));
                            }
                            if ("damage_2".equals(s.keyframeData().getInstructions())) {
                                player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
                                ClientPlayNetworking.send(new SkillPayload(
                                        "damage_2", this.getId()
                                ));
                            }
                        })
        );
    }
    private PlayState animationController(final AnimationTest<VillagerIronGolemEntity> state) {
        if (state.isMoving()) {
            return state.setAndContinue(WALK_ANIM);
        } else {
            return state.setAndContinue(IDEA_ANIM);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
