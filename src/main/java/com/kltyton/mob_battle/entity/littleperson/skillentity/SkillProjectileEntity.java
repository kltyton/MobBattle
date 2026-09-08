package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SkillProjectileEntity extends Projectile implements GeoEntity {
    private static final int OWNER_RESOLUTION_GRACE_TICKS = 5;
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlayAndHold("attack");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final Set<UUID> hitEntities = new HashSet<>();

    private float physicalDamage;
    private float magicDamage;
    private boolean pierceEntities;
    private boolean pierceBlocks;
    private boolean explodeOnHit;
    private int maxAge = 40;
    private double explosionRadius = 3.0D;
    private float ownerHealOnHit;
    private boolean droppedDown;
    private int ownerResolutionTicks;

    public SkillProjectileEntity(EntityType<? extends SkillProjectileEntity> entityType, Level world) {
        super(entityType, world);
        this.noPhysics = true;
    }

    public SkillProjectileEntity configure(LivingEntity owner, Vec3 position, Vec3 velocity, float physicalDamage, float magicDamage,
                                           boolean pierceEntities, boolean pierceBlocks, boolean explodeOnHit, int maxAge) {
        this.setOwner(owner);
        this.setPos(position);
        this.setDeltaMovement(velocity);
        this.physicalDamage = physicalDamage;
        this.magicDamage = magicDamage;
        this.pierceEntities = pierceEntities;
        this.pierceBlocks = pierceBlocks;
        this.explodeOnHit = explodeOnHit;
        this.maxAge = normalizeMaxAge(maxAge);
        this.droppedDown = false;
        this.noPhysics = pierceBlocks;
        return this;
    }

    public void dropDown() {
        this.droppedDown = true;
        this.noPhysics = false;
        this.setNoGravity(false);
        this.setDeltaMovement(0.0D, -0.85D, 0.0D);
        this.hurtMarked = true;
    }

    public void setExplosionRadius(double explosionRadius) {
        this.explosionRadius = nonNegativeFinite(explosionRadius, 0.0D);
    }

    public void setOwnerHealOnHit(float ownerHealOnHit) {
        this.ownerHealOnHit = nonNegativeFinite(ownerHealOnHit, 0.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        this.physicalDamage = nonNegativeFinite(view.getFloatOr("PhysicalDamage", 0.0F), 0.0F);
        this.magicDamage = nonNegativeFinite(view.getFloatOr("MagicDamage", 0.0F), 0.0F);
        this.pierceEntities = view.getBooleanOr("PierceEntities", false);
        this.pierceBlocks = view.getBooleanOr("PierceBlocks", false);
        this.explodeOnHit = view.getBooleanOr("ExplodeOnHit", false);
        this.maxAge = normalizeMaxAge(view.getIntOr("MaxAge", 40));
        this.explosionRadius = nonNegativeFinite(view.getDoubleOr("ExplosionRadius", 3.0D), 0.0D);
        this.ownerHealOnHit = nonNegativeFinite(view.getFloatOr("OwnerHealOnHit", 0.0F), 0.0F);
        this.tickCount = Math.max(0, view.getIntOr("Age", 0));
        this.droppedDown = view.getBooleanOr("DropDown", false);
        this.hitEntities.clear();
        view.read("HitEntities", UUIDUtil.CODEC_SET).ifPresent(this.hitEntities::addAll);
        this.noPhysics = this.droppedDown ? false : this.pierceBlocks;
        this.ownerResolutionTicks = 0;
        if (this.owner == null) {
            this.discard();
        }
        if (this.droppedDown) {
            this.setNoGravity(false);
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        view.putFloat("PhysicalDamage", this.physicalDamage);
        view.putFloat("MagicDamage", this.magicDamage);
        view.putBoolean("PierceEntities", this.pierceEntities);
        view.putBoolean("PierceBlocks", this.pierceBlocks);
        view.putBoolean("ExplodeOnHit", this.explodeOnHit);
        view.putInt("MaxAge", this.maxAge);
        view.putDouble("ExplosionRadius", this.explosionRadius);
        view.putFloat("OwnerHealOnHit", this.ownerHealOnHit);
        view.putInt("Age", this.tickCount);
        view.putInt("RemainingLife", Math.max(0, this.maxAge - this.tickCount));
        view.putBoolean("DropDown", this.droppedDown);
        view.store("HitEntities", UUIDUtil.CODEC_SET, Set.copyOf(this.hitEntities));
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide() && this.resolveCombatOwner() == null) {
            if (this.owner == null || ++this.ownerResolutionTicks > OWNER_RESOLUTION_GRACE_TICKS) {
                this.discard();
            }
            return;
        }
        this.ownerResolutionTicks = 0;
        super.tick();
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (this.level().isClientSide()) {
            return;
        }
        if (this.tickCount > this.maxAge) {
            this.discard();
            return;
        }
        if (!this.pierceBlocks && this.touchesSolidBlock()) {
            if (this.explodeOnHit) {
                explode();
            } else {
                this.discard();
            }
            return;
        }
        hitNearbyTargets();
    }

    private boolean touchesSolidBlock() {
        return this.level().getBlockState(this.blockPosition()).isRedstoneConductor(this.level(), this.blockPosition());
    }

    private void hitNearbyTargets() {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        LivingEntity owner = this.resolveCombatOwner();
        if (owner == null) {
            this.discard();
            return;
        }
        AABB box = this.getBoundingBox().inflate(0.45D);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, box,
                living -> EntityQueries.isValidSummonCombatTarget(this, owner, living))) {
            if (!this.hitEntities.add(target.getUUID())) {
                continue;
            }
            damageTarget(world, target, owner);
            if (this.explodeOnHit) {
                explode();
                return;
            }
            if (!this.pierceEntities) {
                this.discard();
                return;
            }
        }
    }

    private void damageTarget(ServerLevel world, LivingEntity target, LivingEntity owner) {
        if (owner == null || owner.isRemoved() || !EntityQueries.isValidSummonCombatTarget(this, owner, target)) {
            return;
        }
        if (this.physicalDamage > 0.0F) {
            target.hurtServer(world, this.damageSources().mobProjectile(this, owner), this.physicalDamage);
        }
        if (this.magicDamage > 0.0F) {
            target.hurtServer(world, this.damageSources().indirectMagic(this, owner), this.magicDamage);
        }
        if (this.ownerHealOnHit > 0.0F && owner.isAlive()) {
            owner.heal(this.ownerHealOnHit);
        }
    }

    private void explode() {
        if (!(this.level() instanceof ServerLevel world)) {
            this.discard();
            return;
        }
        LivingEntity owner = this.resolveCombatOwner();
        if (owner == null) {
            this.discard();
            return;
        }
        world.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState()),
                this.getX(), this.getY(), this.getZ(), 35, 0.8D, 0.6D, 0.8D, 0.12D);
        world.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), this.getSoundSource(), 1.2F, 0.9F);
        AABB box = this.getBoundingBox().inflate(this.explosionRadius);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, box,
                living -> EntityQueries.isValidSummonCombatTarget(this, owner, living))) {
            damageTarget(world, target, owner);
        }
        this.discard();
    }

    private LivingEntity resolveCombatOwner() {
        Entity owner = this.getOwner();
        return owner instanceof LivingEntity livingOwner && !livingOwner.isRemoved() ? livingOwner : null;
    }

    private int normalizeMaxAge(int age) {
        int normalized = Math.max(0, age);
        if (this.getType() == ModEntities.BLOOD_SWORD_ENERGY) {
            return Math.min(normalized, 14);
        }
        if (this.getType() == ModEntities.ICE_SWORD_ENERGY) {
            return Math.min(normalized, 34);
        }
        return normalized;
    }

    private static float nonNegativeFinite(float value, float fallback) {
        return Float.isFinite(value) && value >= 0.0F ? value : fallback;
    }

    private static double nonNegativeFinite(double value, double fallback) {
        return Double.isFinite(value) && value >= 0.0D ? value : fallback;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 0, state -> state.setAndContinue(ATTACK_ANIM)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
