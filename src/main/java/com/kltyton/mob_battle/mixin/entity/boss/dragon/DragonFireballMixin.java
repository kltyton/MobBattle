package com.kltyton.mob_battle.mixin.entity.boss.dragon;

import com.kltyton.mob_battle.entity.misc.ModifiedDragonBreathCloud;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DragonFireball.class)
public abstract class DragonFireballMixin extends AbstractHurtingProjectile {

    protected DragonFireballMixin(EntityType<? extends AbstractHurtingProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Redirect(method = "onHit", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/Level;DDD)Lnet/minecraft/world/entity/AreaEffectCloud;"))
    private AreaEffectCloud redirectCloud(Level world, double x, double y, double z) {
        ModifiedDragonBreathCloud cloud = new ModifiedDragonBreathCloud(world, x, y, z);
        // 复制原版设置
        DragonFireball self = (DragonFireball)(Object)this;
        if (self.getOwner() instanceof LivingEntity ownerEntity) {
            cloud.setOwner(ownerEntity);
        }
        cloud.setCustomParticle(ParticleTypes.DRAGON_BREATH);
        cloud.setRadius(3.0F);
        cloud.setDuration(600);
        cloud.setRadiusPerTick((7.0F - cloud.getRadius()) / cloud.getDuration());
        cloud.setPotionDurationScale(0.25F);

        return cloud;
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.tickCount >= 200) {
                this.discard();
            }
        }
        DragonFireball self = (DragonFireball)(Object)this;

        double x = self.getX();
        double y = self.getY();
        double z = self.getZ();

        var random = self.level().random;

        // 核心拖尾：大量紫色龙息粒子向后
        for (int i = 0; i < 6; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 0.4;
            double offsetY = (random.nextDouble() - 0.5) * 0.4;
            double offsetZ = (random.nextDouble() - 0.5) * 0.4;

            self.level().addParticle(
                    ParticleTypes.DRAGON_BREATH,
                    x + offsetX, y + offsetY, z + offsetZ,
                    -self.getDeltaMovement().x * 0.1, -self.getDeltaMovement().y * 0.1, -self.getDeltaMovement().z * 0.1
            );
        }

        // 炫酷加强：加末影粒子 + 电火花感
        for (int i = 0; i < 2; i++) {
            self.level().addParticle(
                    ParticleTypes.END_ROD,
                    x, y, z,
                    (random.nextDouble() - 0.5) * 0.15,
                    0.08,
                    (random.nextDouble() - 0.5) * 0.15
            );
        }

        self.level().addParticle(
                ParticleTypes.ELECTRIC_SPARK,
                x, y + 0.1, z, 0, 0, 0
        );
    }
}
