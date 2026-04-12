package com.kltyton.mob_battle.entity.littleperson.militia.soldier;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.general.GeneralEntityOnlyOneSkill;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;

public class LittlePersonSoldierEntity extends HostileEntity implements LittlePersonEntity, GeneralEntityOnlyOneSkill<LittlePersonSoldierEntity> {

    public static final TrackedData<Boolean> IS_CHARGING =
            DataTracker.registerData(LittlePersonSoldierEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HAS_SKILL =
            DataTracker.registerData(LittlePersonSoldierEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> SKILL_COOLDOWN =
            DataTracker.registerData(LittlePersonSoldierEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final int MAX_CHARGE_TICKS = 12;
    private static final double CHARGE_SPEED = 0.8D;

    private Vec3d chargeDirection = Vec3d.ZERO;
    private int chargeTicks = 0;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenPlayAndHold("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenPlayAndHold("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");

    public LittlePersonSoldierEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setAiDisabled(false);
        this.setHasSkill(false);
        this.setSkillCooldown(getCooldownTime());
    }

    public int getCooldownTime() {
        return 10;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SKILL, false);
        builder.add(SKILL_COOLDOWN, getCooldownTime());
        builder.add(IS_CHARGING, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));

        this.targetSelector.add(3, new ActiveTargetGoal<>(this, GolemEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, WarriorVillager.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false,
                (entity, world) -> entity instanceof Monster && !(entity instanceof LittlePersonEntity)));
    }

    public static DefaultAttributeContainer.Builder createLittlePersonMilitiaAttributes() {
        return LittlePersonEntity.createLittlePersonAttributes()
                .add(EntityAttributes.MAX_HEALTH, 300.0)
                .add(EntityAttributes.FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.ATTACK_DAMAGE, 30.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.2);
    }

    @Override
    public boolean hasSkill() {
        return this.getDataTracker().get(HAS_SKILL);
    }

    @Override
    public void setHasSkill(boolean hasSkill) {
        this.getDataTracker().set(HAS_SKILL, hasSkill);
    }

    public int getSkillCooldown() {
        return this.getDataTracker().get(SKILL_COOLDOWN);
    }

    public void setSkillCooldown(int cooldown) {
        this.getDataTracker().set(SKILL_COOLDOWN, cooldown);
    }

    public boolean isCharging() {
        return this.getDataTracker().get(IS_CHARGING);
    }

    public void setCharging(boolean charging) {
        this.getDataTracker().set(IS_CHARGING, charging);
    }

    @Override
    public void heal() {
        this.heal(3.0F);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            if (this.age % 20 == 0) {
                this.heal();
            }

            if (canSkill()) {
                performSkill();
            }

            if (isCharging()) {
                performChargeMovement();
            } else if (!hasSkill()) {
                this.setAiDisabled(false);
                int cd = getSkillCooldown();
                if (cd > 0) {
                    setSkillCooldown(cd - 1);
                }
            }
        }
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isAiDisabled()) {
            super.takeKnockback(strength, x, z);
        }
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        return false;
    }

    @Override
    public boolean canSkill() {
        if (!ModSkillEntityType.canSkill(this)) {
            return false;
        }
        return !this.getWorld().isClient() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }

    public void performSkill() {
        if (!lockChargeDirectionToTarget()) {
            return;
        }

        this.setHasSkill(true);
        this.setAiDisabled(true);
        this.setSkillCooldown(getCooldownTime());
        this.triggerAnim("attack_controller", "attack");
    }

    private boolean lockChargeDirectionToTarget() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        Vec3d dir = target.getPos().subtract(this.getPos());
        dir = new Vec3d(dir.x, 0.0D, dir.z);

        if (dir.lengthSquared() < 1.0E-6D) {
            return false;
        }

        this.chargeDirection = dir.normalize();
        faceToDirection(this.chargeDirection);
        return true;
    }

    private void faceToDirection(Vec3d direction) {
        if (direction.lengthSquared() < 1.0E-6D) {
            return;
        }

        float yaw = (float) (MathHelper.atan2(direction.z, direction.x) * 180.0D / Math.PI) - 90.0F;
        this.setYaw(yaw);
        this.setBodyYaw(yaw);
        this.setHeadYaw(yaw);
        this.setPitch(0.0F);
    }

    private void performChargeMovement() {
        if (this.getWorld().isClient) {
            return;
        }

        if (this.chargeTicks <= 0) {
            stopSkill();
            return;
        }

        this.chargeTicks--;

        faceToDirection(this.chargeDirection);

        Vec3d velocity = this.chargeDirection.multiply(CHARGE_SPEED);
        this.move(MovementType.SELF, velocity);

        if (this.horizontalCollision) {
            stopSkill();
            return;
        }

        Box damageBox = this.getBoundingBox().expand(0.5D, 0.2D, 0.5D);
        List<Entity> targets = this.getWorld().getOtherEntities(this, damageBox,
                target -> target instanceof LivingEntity
                        && target.isAlive()
                        && target != this
                        && !target.isTeammate(this));

        float damageAmount = (float) this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
        for (Entity target : targets) {
            tryAttackBaseDamage((ServerWorld) this.getWorld(), target, damageAmount);
        }
    }

    public boolean tryAttackBaseDamage(ServerWorld world, Entity target, float damage) {
        if (!ModSkillEntityType.canSkill(this)) {
            return false;
        }

        float f = damage;
        ItemStack itemStack = this.getWeaponStack();
        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this))
                .orElse(this.getDamageSources().mobAttack(this));

        f = EnchantmentHelper.getDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getBonusAttackDamage(target, f, damageSource);

        boolean bl = target.damage(world, damageSource, f);
        if (bl) {
            float g = this.getAttackKnockbackAgainst(target, damageSource);
            if (g > 0.0F && target instanceof LivingEntity livingEntity) {
                livingEntity.takeKnockback(
                        g * 0.5F,
                        MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)),
                        -MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0))
                );
                this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof LivingEntity livingEntity) {
                itemStack.postHit(livingEntity, this);
            }

            EnchantmentHelper.onTargetDamaged(world, target, damageSource);
            this.onAttacking(target);
            this.playAttackSound();
        }

        return bl;
    }

    @Override
    public void runSkill(LittlePersonSoldierEntity entity) {
        if (this.getWorld().isClient) {
            return;
        }

        if (!lockChargeDirectionToTarget()) {
            stopSkill();
            return;
        }

        this.chargeTicks = MAX_CHARGE_TICKS;
        this.setCharging(true);
    }

    @Override
    public void stopSkill() {
        this.setHasSkill(false);
        this.setCharging(false);
        this.setAiDisabled(false);
        this.chargeDirection = Vec3d.ZERO;
        this.chargeTicks = 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 5, this::mainController));

        controllers.add(new AnimationController<>("attack_controller", animTest -> {
                    if (animTest.controller().getAnimationState() == AnimationController.State.STOPPED) {
                        if (this.hasSkill()) {
                            ClientPlayNetworking.send(new SkillPayload("stop", this.getId()));
                        }
                    }
                    return PlayState.STOP;
                })
                        .triggerableAnim("attack", ATTACK_ANIM)
                        .setCustomInstructionKeyframeHandler(s -> {
                            if ("runAttack;".equals(s.keyframeData().getInstructions())) {
                                ClientPlayNetworking.send(new SkillPayload("attack", this.getId()));
                            }
                        })
        );
    }

    private PlayState mainController(final AnimationTest<LittlePersonSoldierEntity> event) {
        return event.isMoving() ? event.setAndContinue(WALK_ANIM) : event.setAndContinue(IDLE_ANIM);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
