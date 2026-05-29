package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.utils.EntityUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.Set;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SkillProjectileEntity extends Projectile implements GeoEntity {
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlayAndHold("attack");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final Set<Integer> hitEntities = new HashSet<>();

    private float physicalDamage;
    private float magicDamage;
    private boolean pierceEntities;
    private boolean pierceBlocks;
    private boolean explodeOnHit;
    private int maxAge = 40;
    private double explosionRadius = 3.0D;

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
        this.maxAge = maxAge;
        if (this.getType() == ModEntities.BLOOD_SWORD_ENERGY) {
            this.maxAge = Math.min(this.maxAge, 14);
        } else if (this.getType() == ModEntities.ICE_SWORD_ENERGY) {
            this.maxAge = Math.min(this.maxAge, 34);
        }
        this.noPhysics = pierceBlocks;
        return this;
    }

    public void dropDown() {
        this.noPhysics = false;
        this.setNoGravity(false);
        this.setDeltaMovement(0.0D, -0.85D, 0.0D);
        this.hurtMarked = true;
    }

    public void setExplosionRadius(double explosionRadius) {
        this.explosionRadius = explosionRadius;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
    }

    @Override
    public void tick() {
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
        Entity owner = this.getOwner();
        AABB box = this.getBoundingBox().inflate(0.45D);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, box,
                living -> EntityUtil.isValidSummonCombatTarget(this, owner, living))) {
            if (!this.hitEntities.add(target.getId())) {
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

    private void damageTarget(ServerLevel world, LivingEntity target, Entity owner) {
        if (!EntityUtil.isValidSummonCombatTarget(this, owner, target)) {
            return;
        }
        if (this.physicalDamage > 0.0F) {
            if (owner instanceof LivingEntity livingOwner) {
                target.hurtServer(world, this.damageSources().mobProjectile(this, livingOwner), this.physicalDamage);
            } else {
                target.hurtServer(world, this.damageSources().magic(), this.physicalDamage);
            }
        }
        if (this.magicDamage > 0.0F) {
            target.hurtServer(world, this.damageSources().indirectMagic(this, owner == null ? this : owner), this.magicDamage);
        }
    }

    private void explode() {
        if (!(this.level() instanceof ServerLevel world)) {
            this.discard();
            return;
        }
        Entity owner = this.getOwner();
        world.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, net.minecraft.world.level.block.Blocks.ICE.defaultBlockState()),
                this.getX(), this.getY(), this.getZ(), 35, 0.8D, 0.6D, 0.8D, 0.12D);
        world.playSound(null, this.blockPosition(), SoundEvents.GLASS_BREAK, this.getSoundSource(), 1.1F, 0.8F);
        AABB box = this.getBoundingBox().inflate(this.explosionRadius);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, box,
                living -> EntityUtil.isValidSummonCombatTarget(this, owner, living))) {
            damageTarget(world, target, owner);
        }
        this.discard();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", 5, state -> state.setAndContinue(ATTACK_ANIM)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
