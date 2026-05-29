package com.kltyton.mob_battle.entity.littleperson.militia.soldier;

import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.ModSkillEntityType;
import com.kltyton.mob_battle.entity.general.GeneralEntityOnlyOneSkill;
import com.kltyton.mob_battle.entity.littleperson.LittlePersonEntity;
import com.kltyton.mob_battle.entity.villager.warriorvillager.WarriorVillager;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;

public class LittlePersonSoldierEntity extends Monster implements LittlePersonEntity, GeneralEntityOnlyOneSkill<LittlePersonSoldierEntity> {

    public static final EntityDataAccessor<Boolean> IS_CHARGING =
            SynchedEntityData.defineId(LittlePersonSoldierEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> HAS_SKILL =
            SynchedEntityData.defineId(LittlePersonSoldierEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SKILL_COOLDOWN =
            SynchedEntityData.defineId(LittlePersonSoldierEntity.class, EntityDataSerializers.INT);

    private static final int MAX_CHARGE_TICKS = 12;
    private static final double CHARGE_SPEED = 0.8D;

    private Vec3 chargeDirection = Vec3.ZERO;
    private int chargeTicks = 0;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenPlayAndHold("idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenPlayAndHold("walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");

    public LittlePersonSoldierEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.setNoAi(false);
        this.setHasSkill(false);
        this.setSkillCooldown(getCooldownTime());
    }

    public int getCooldownTime() {
        return 10;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_SKILL, false);
        builder.define(SKILL_COOLDOWN, getCooldownTime());
        builder.define(IS_CHARGING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, WarriorVillager.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false,
                (entity, world) -> entity instanceof Enemy && !(entity instanceof LittlePersonEntity)));
    }

    public static AttributeSupplier.Builder createLittlePersonMilitiaAttributes() {
        return LittlePersonEntity.createLittlePersonAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 30.0)
                .add(ModEntityAttributes.DAMAGE_REDUCTION, 0.2);
    }

    @Override
    public boolean hasSkill() {
        return this.getEntityData().get(HAS_SKILL);
    }

    @Override
    public void setHasSkill(boolean hasSkill) {
        this.getEntityData().set(HAS_SKILL, hasSkill);
    }

    public int getSkillCooldown() {
        return this.getEntityData().get(SKILL_COOLDOWN);
    }

    public void setSkillCooldown(int cooldown) {
        this.getEntityData().set(SKILL_COOLDOWN, cooldown);
    }

    public boolean isCharging() {
        return this.getEntityData().get(IS_CHARGING);
    }

    public void setCharging(boolean charging) {
        this.getEntityData().set(IS_CHARGING, charging);
    }

    @Override
    public void heal() {
        this.heal(3.0F);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.tickCount % 20 == 0) {
                this.heal();
            }

            if (canSkill()) {
                performSkill();
            }

            if (isCharging()) {
                performChargeMovement();
            } else if (!hasSkill()) {
                this.setNoAi(false);
                int cd = getSkillCooldown();
                if (cd > 0) {
                    setSkillCooldown(cd - 1);
                }
            }
        }
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (!hasSkill() || !this.isNoAi()) {
            super.knockback(strength, x, z);
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        return false;
    }

    @Override
    public boolean canSkill() {
        if (!ModSkillEntityType.canSkill(this)) {
            return false;
        }
        return !this.level().isClientSide() && !hasSkill() && getSkillCooldown() == 0 && this.getTarget() != null;
    }

    public void performSkill() {
        if (!lockChargeDirectionToTarget()) {
            return;
        }

        this.setHasSkill(true);
        this.setNoAi(true);
        this.setSkillCooldown(getCooldownTime());
        this.triggerAnim("attack_controller", "attack");
    }

    private boolean lockChargeDirectionToTarget() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        Vec3 dir = target.position().subtract(this.position());
        dir = new Vec3(dir.x, 0.0D, dir.z);

        if (dir.lengthSqr() < 1.0E-6D) {
            return false;
        }

        this.chargeDirection = dir.normalize();
        faceToDirection(this.chargeDirection);
        return true;
    }

    private void faceToDirection(Vec3 direction) {
        if (direction.lengthSqr() < 1.0E-6D) {
            return;
        }

        float yaw = (float) (Mth.atan2(direction.z, direction.x) * 180.0D / Math.PI) - 90.0F;
        this.setYRot(yaw);
        this.setYBodyRot(yaw);
        this.setYHeadRot(yaw);
        this.setXRot(0.0F);
    }

    private void performChargeMovement() {
        if (this.level().isClientSide) {
            return;
        }

        if (this.chargeTicks <= 0) {
            stopSkill();
            return;
        }

        this.chargeTicks--;

        faceToDirection(this.chargeDirection);

        Vec3 velocity = this.chargeDirection.scale(CHARGE_SPEED);
        this.move(MoverType.SELF, velocity);

        if (this.horizontalCollision) {
            stopSkill();
            return;
        }

        AABB damageBox = this.getBoundingBox().inflate(0.5D, 0.2D, 0.5D);
        List<Entity> targets = this.level().getEntities(this, damageBox,
                target -> target instanceof LivingEntity
                        && target.isAlive()
                        && target != this
                        && !target.isAlliedTo(this));

        float damageAmount = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        for (Entity target : targets) {
            tryAttackBaseDamage((ServerLevel) this.level(), target, damageAmount);
        }
    }

    public boolean tryAttackBaseDamage(ServerLevel world, Entity target, float damage) {
        if (!ModSkillEntityType.canSkill(this)) {
            return false;
        }

        float f = damage;
        ItemStack itemStack = this.getWeaponItem();
        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this))
                .orElse(this.damageSources().mobAttack(this));

        f = EnchantmentHelper.modifyDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getAttackDamageBonus(target, f, damageSource);

        boolean bl = target.hurtServer(world, damageSource, f);
        if (bl) {
            float g = this.getKnockback(target, damageSource);
            if (g > 0.0F && target instanceof LivingEntity livingEntity) {
                livingEntity.knockback(
                        g * 0.5F,
                        Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)),
                        -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0))
                );
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof LivingEntity livingEntity) {
                itemStack.hurtEnemy(livingEntity, this);
            }

            EnchantmentHelper.doPostAttackEffects(world, target, damageSource);
            this.setLastHurtMob(target);
            this.playAttackSound();
        }

        return bl;
    }

    @Override
    public void runSkill(LittlePersonSoldierEntity entity) {
        if (this.level().isClientSide) {
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
        this.setNoAi(false);
        this.chargeDirection = Vec3.ZERO;
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
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
