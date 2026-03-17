package com.kltyton.mob_battle.entity.customfireball;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MagmaLobsterBigFireballEntity extends CustomFireballEntity implements GeoEntity {
    public MagmaLobsterBigFireballEntity(EntityType<? extends MagmaLobsterBigFireballEntity> entityType, World world) {
        super(entityType, world);
        this.damage = 50.0F;
        this.power = 0.0F;
        this.isExplosive = false;
    }

    public MagmaLobsterBigFireballEntity(EntityType<? extends MagmaLobsterBigFireballEntity> entityType, World world, LivingEntity owner) {
        super(entityType, world, owner, 0.0F, false, 50.0F);
        this.damage = 50.0F;
        this.power = 0.0F;
        this.isExplosive = false;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

        serverWorld.spawnParticles(
                ParticleTypes.FLAME,
                this.getX(), this.getY(), this.getZ(),
                50,
                0.6D, 0.6D, 0.6D,
                0.03D
        );
        serverWorld.spawnParticles(
                ParticleTypes.SMOKE,
                this.getX(), this.getY(), this.getZ(),
                25,
                0.5D, 0.5D, 0.5D,
                0.02D
        );

        Entity owner = this.getOwner();

        for (LivingEntity living : serverWorld.getEntitiesByClass(
                LivingEntity.class,
                this.getBoundingBox().expand(3.0D),
                entity -> entity.isAlive() && entity != owner
        )) {
            DamageSource explosionSource = this.getDamageSources().explosion(this, owner);
            DamageSource fireballSource = this.getDamageSources().fireball(this, owner);

            living.damage(serverWorld, explosionSource, 50.0F);
            living.damage(serverWorld, fireballSource, 50.0F);
            living.setOnFireFor(5);
            EnchantmentHelper.onTargetDamaged(serverWorld, living, fireballSource);
        }

        this.discard();
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", s -> s.setAndContinue(IDEA_ANIM)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}