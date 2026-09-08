package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.support.EntityQueries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public class SkillVisualEntity extends Entity implements GeoEntity {
    private static final int OWNER_RESOLUTION_GRACE_TICKS = 5;
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(SkillVisualEntity.class, EntityDataSerializers.INT);
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlayAndHold("attack");
    private static final RawAnimation ATTACK_1_ANIM = RawAnimation.begin().thenPlayAndHold("attack1");
    private static final RawAnimation ATTACK_2_ANIM = RawAnimation.begin().thenPlayAndHold("attack2");
    private static final RawAnimation ATTACK_3_ANIM = RawAnimation.begin().thenPlayAndHold("attack3");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Nullable
    private LivingEntity owner;
    @Nullable
    private EntityReference<LivingEntity> ownerReference;
    private float damage;
    private int damageAge = -1;
    private int maxAge = 30;
    private double radius = 1.0D;
    private boolean continuousCollisionDamage;
    private boolean ownerCanBeHit;
    private int ownerResolutionTicks;

    public SkillVisualEntity(EntityType<? extends SkillVisualEntity> entityType, Level world) {
        super(entityType, world);
    }

    public SkillVisualEntity configure(LivingEntity owner, float damage, int damageAge, int maxAge, double radius, int variant) {
        if (owner == null) {
            this.owner = null;
            this.ownerReference = null;
            this.discard();
            return this;
        }
        this.owner = owner;
        this.ownerReference = EntityReference.of(owner);
        this.damage = damage;
        this.damageAge = damageAge;
        this.maxAge = maxAge;
        this.radius = radius;
        this.continuousCollisionDamage = false;
        this.ownerCanBeHit = false;
        this.entityData.set(VARIANT, variant);
        return this;
    }

    /** 仅供明确允许召唤物命中主人的技能使用；默认仍保护主人。 */
    public SkillVisualEntity allowOwnerHit() {
        this.ownerCanBeHit = true;
        return this;
    }

    public SkillVisualEntity configureCollisionDamage(LivingEntity owner, float damage, int maxAge, double radius, int variant) {
        configure(owner, damage, -1, maxAge, radius, variant);
        this.continuousCollisionDamage = true;
        return this;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(VARIANT, 0);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        this.damage = nonNegativeFinite(view.getFloatOr("Damage", 0.0F), 0.0F);
        this.damageAge = Math.max(-1, view.getIntOr("DamageAge", -1));
        this.maxAge = Math.max(0, view.getIntOr("MaxAge", 30));
        this.radius = nonNegativeFinite(view.getDoubleOr("Radius", 1.0D), 0.0D);
        this.continuousCollisionDamage = view.getBooleanOr("ContinuousCollisionDamage", false);
        this.ownerCanBeHit = view.getBooleanOr("OwnerCanBeHit", false);
        this.entityData.set(VARIANT, Math.max(0, view.getIntOr("Variant", 0)));
        this.tickCount = Math.max(0, view.getIntOr("Age", 0));
        this.ownerReference = EntityReference.read(view, "Owner");
        this.owner = EntityReference.getLivingEntity(this.ownerReference, this.level());
        this.ownerResolutionTicks = 0;
        if (this.ownerReference == null || this.tickCount > this.maxAge) {
            this.discard();
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        if (this.ownerReference == null) {
            this.discard();
            return;
        }
        EntityReference.store(this.ownerReference, view, "Owner");
        view.putFloat("Damage", this.damage);
        view.putInt("DamageAge", this.damageAge);
        view.putInt("MaxAge", this.maxAge);
        view.putDouble("Radius", this.radius);
        view.putBoolean("ContinuousCollisionDamage", this.continuousCollisionDamage);
        view.putBoolean("OwnerCanBeHit", this.ownerCanBeHit);
        view.putInt("Variant", this.entityData.get(VARIANT));
        view.putInt("Age", this.tickCount);
        view.putInt("RemainingLife", Math.max(0, this.maxAge - this.tickCount));
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            LivingEntity owner = resolveOwner();
            if (owner == null) {
                if (this.ownerReference == null || ++this.ownerResolutionTicks > OWNER_RESOLUTION_GRACE_TICKS) {
                    this.discard();
                }
                return;
            }
            this.ownerResolutionTicks = 0;
            if (this.tickCount > this.maxAge) {
                this.discard();
                return;
            }
            if (this.damageAge >= 0 && this.tickCount == this.damageAge) {
                damageNearby();
            }
            if (this.continuousCollisionDamage) {
                damageNearby();
            }
        }
    }

    private void damageNearby() {
        if (!(this.level() instanceof ServerLevel world) || this.damage <= 0.0F) {
            return;
        }
        LivingEntity owner = this.resolveOwner();
        if (owner == null || !owner.isAlive()) {
            return;
        }
        AABB box = this.getBoundingBox().inflate(this.radius);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, box,
                living -> this.ownerCanBeHit && living == owner
                        || EntityQueries.isValidSummonCombatTarget(this, owner, living))) {
            target.invulnerableTime = 0;
            target.hurtServer(world, owner.damageSources().mobAttack(owner), this.damage);
            target.invulnerableTime = 0;
        }
    }

    @Nullable
    private LivingEntity resolveOwner() {
        if (this.owner != null && !this.owner.isRemoved()) {
            return this.owner;
        }
        this.owner = EntityReference.getLivingEntity(this.ownerReference, this.level());
        return this.owner;
    }

    /** 返回当前可解析的技能主人，供服务端诊断和行为测试核对实体归属。 */
    @Nullable
    public LivingEntity getSkillOwner() {
        return resolveOwner();
    }

    private static float nonNegativeFinite(float value, float fallback) {
        return Float.isFinite(value) && value >= 0.0F ? value : fallback;
    }

    private static double nonNegativeFinite(double value, double fallback) {
        return Double.isFinite(value) && value >= 0.0D ? value : fallback;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 0, state -> {
            int variant = this.entityData.get(VARIANT);
            if (variant == 1) {
                return state.setAndContinue(ATTACK_1_ANIM);
            }
            if (variant == 2) {
                return state.setAndContinue(ATTACK_2_ANIM);
            }
            if (variant == 3) {
                return state.setAndContinue(ATTACK_3_ANIM);
            }
            return state.setAndContinue(ATTACK_ANIM);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
