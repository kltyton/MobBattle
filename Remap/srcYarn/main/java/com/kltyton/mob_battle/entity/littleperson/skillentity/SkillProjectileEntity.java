package com.kltyton.mob_battle.entity.littleperson.skillentity;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.Set;

public class SkillProjectileEntity extends ProjectileEntity implements GeoEntity {
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

    public SkillProjectileEntity(EntityType<? extends SkillProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.noClip = true;
    }

    public SkillProjectileEntity configure(LivingEntity owner, Vec3d position, Vec3d velocity, float physicalDamage, float magicDamage,
                                           boolean pierceEntities, boolean pierceBlocks, boolean explodeOnHit, int maxAge) {
        this.setOwner(owner);
        this.setPosition(position);
        this.setVelocity(velocity);
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
        this.noClip = pierceBlocks;
        return this;
    }

    public void dropDown() {
        this.noClip = false;
        this.setNoGravity(false);
        this.setVelocity(0.0D, -0.85D, 0.0D);
        this.velocityModified = true;
    }

    public void setExplosionRadius(double explosionRadius) {
        this.explosionRadius = explosionRadius;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    protected void readCustomData(ReadView view) {
    }

    @Override
    protected void writeCustomData(WriteView view) {
    }

    @Override
    public void tick() {
        super.tick();
        this.move(MovementType.SELF, this.getVelocity());
        if (this.getWorld().isClient()) {
            return;
        }
        if (this.age > this.maxAge) {
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
        return this.getWorld().getBlockState(this.getBlockPos()).isSolidBlock(this.getWorld(), this.getBlockPos());
    }

    private void hitNearbyTargets() {
        if (!(this.getWorld() instanceof ServerWorld world)) {
            return;
        }
        Entity owner = this.getOwner();
        Box box = this.getBoundingBox().expand(0.45D);
        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, box,
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

    private void damageTarget(ServerWorld world, LivingEntity target, Entity owner) {
        if (!EntityUtil.isValidSummonCombatTarget(this, owner, target)) {
            return;
        }
        if (this.physicalDamage > 0.0F) {
            if (owner instanceof LivingEntity livingOwner) {
                target.damage(world, this.getDamageSources().mobProjectile(this, livingOwner), this.physicalDamage);
            } else {
                target.damage(world, this.getDamageSources().magic(), this.physicalDamage);
            }
        }
        if (this.magicDamage > 0.0F) {
            target.damage(world, this.getDamageSources().indirectMagic(this, owner == null ? this : owner), this.magicDamage);
        }
    }

    private void explode() {
        if (!(this.getWorld() instanceof ServerWorld world)) {
            this.discard();
            return;
        }
        Entity owner = this.getOwner();
        world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, net.minecraft.block.Blocks.ICE.getDefaultState()),
                this.getX(), this.getY(), this.getZ(), 35, 0.8D, 0.6D, 0.8D, 0.12D);
        world.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, this.getSoundCategory(), 1.1F, 0.8F);
        Box box = this.getBoundingBox().expand(this.explosionRadius);
        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, box,
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
