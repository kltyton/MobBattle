package com.kltyton.mob_battle.entity.customfireball;

import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MagmaLobsterBigFireballEntity extends CustomFireballEntity implements GeoEntity {
    public MagmaLobsterBigFireballEntity(EntityType<? extends MagmaLobsterBigFireballEntity> entityType, Level world) {
        super(entityType, world);
        this.damage = 50.0F;
        this.power = 0.0F;
        this.isExplosive = false;
    }

    public MagmaLobsterBigFireballEntity(EntityType<? extends MagmaLobsterBigFireballEntity> entityType, Level world, LivingEntity owner) {
        super(entityType, world, owner, 0.0F, false, 50.0F);
        this.damage = 50.0F;
        this.power = 0.0F;
        this.isExplosive = false;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (!(this.level() instanceof ServerLevel serverWorld)) return;

        serverWorld.sendParticles(
                ParticleTypes.FLAME,
                this.getX(), this.getY(), this.getZ(),
                50,
                0.6D, 0.6D, 0.6D,
                0.03D
        );
        serverWorld.sendParticles(
                ParticleTypes.SMOKE,
                this.getX(), this.getY(), this.getZ(),
                25,
                0.5D, 0.5D, 0.5D,
                0.02D
        );

        Entity owner = this.getOwner();

        for (LivingEntity living : serverWorld.getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(3.0D),
                entity -> EntityUtil.isValidSummonCombatTarget(this, owner, entity)
        )) {
            DamageSource explosionSource = this.damageSources().explosion(this, owner);
            DamageSource fireballSource = this.damageSources().fireball(this, owner);

            living.hurtServer(serverWorld, explosionSource, 50.0F);
            living.hurtServer(serverWorld, fireballSource, 50.0F);
            living.igniteForSeconds(5);
            EnchantmentHelper.doPostAttackEffects(serverWorld, living, fireballSource);
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
