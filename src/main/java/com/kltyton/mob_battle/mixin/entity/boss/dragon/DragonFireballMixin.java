package com.kltyton.mob_battle.mixin.entity.boss.dragon;

import com.kltyton.mob_battle.entity.misc.ModifiedDragonBreathCloud;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DragonFireballEntity.class)
public abstract class DragonFireballMixin extends ExplosiveProjectileEntity {

    protected DragonFireballMixin(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "onCollision", at = @At(value = "NEW", target = "(Lnet/minecraft/world/World;DDD)Lnet/minecraft/entity/AreaEffectCloudEntity;"))
    private AreaEffectCloudEntity redirectCloud(World world, double x, double y, double z) {
        ModifiedDragonBreathCloud cloud = new ModifiedDragonBreathCloud(world, x, y, z);
        // 复制原版设置
        DragonFireballEntity self = (DragonFireballEntity)(Object)this;
        if (self.getOwner() instanceof LivingEntity ownerEntity) {
            cloud.setOwner(ownerEntity);
        }
        cloud.setParticleType(ParticleTypes.DRAGON_BREATH);
        cloud.setRadius(3.0F);
        cloud.setDuration(600);
        cloud.setRadiusGrowth((7.0F - cloud.getRadius()) / cloud.getDuration());
        cloud.setPotionDurationScale(0.25F);

        return cloud;
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (this.age >= 200) {
                this.discard();
            }
        }
        DragonFireballEntity self = (DragonFireballEntity)(Object)this;

        double x = self.getX();
        double y = self.getY();
        double z = self.getZ();

        var random = self.getWorld().random;

        // 核心拖尾：大量紫色龙息粒子向后
        for (int i = 0; i < 6; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 0.4;
            double offsetY = (random.nextDouble() - 0.5) * 0.4;
            double offsetZ = (random.nextDouble() - 0.5) * 0.4;

            self.getWorld().addParticleClient(
                    ParticleTypes.DRAGON_BREATH,
                    x + offsetX, y + offsetY, z + offsetZ,
                    -self.getVelocity().x * 0.1, -self.getVelocity().y * 0.1, -self.getVelocity().z * 0.1
            );
        }

        // 炫酷加强：加末影粒子 + 电火花感
        for (int i = 0; i < 2; i++) {
            self.getWorld().addParticleClient(
                    ParticleTypes.END_ROD,
                    x, y, z,
                    (random.nextDouble() - 0.5) * 0.15,
                    0.08,
                    (random.nextDouble() - 0.5) * 0.15
            );
        }

        self.getWorld().addParticleClient(
                ParticleTypes.ELECTRIC_SPARK,
                x, y + 0.1, z, 0, 0, 0
        );
    }
}
