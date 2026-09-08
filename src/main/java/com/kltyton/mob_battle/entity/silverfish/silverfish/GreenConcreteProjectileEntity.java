package com.kltyton.mob_battle.entity.silverfish.silverfish;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.bullet.TrueDamageProjectile;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;

public class GreenConcreteProjectileEntity extends TrueDamageProjectile {
    public GreenConcreteProjectileEntity(EntityType<GreenConcreteProjectileEntity> entityType, Level level) {
        super(entityType, level);
        this.pickup = Pickup.DISALLOWED;
    }

    public GreenConcreteProjectileEntity(Level level, LivingEntity owner) {
        super(ModEntities.GREEN_CONCRETE_PROJECTILE, owner, level, new ItemStack(Blocks.GREEN_CONCRETE), null);
        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0D;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(Blocks.GREEN_CONCRETE);
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        this.discard();
    }

    @Override
    protected void doPostHurtEffects(LivingEntity target) {
        super.doPostHurtEffects(target);
        if (this.level() instanceof ServerLevel world) {
            world.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GREEN_CONCRETE.defaultBlockState()),
                    target.getX(),
                    target.getY(0.5D),
                    target.getZ(),
                    24,
                    0.35D,
                    0.35D,
                    0.35D,
                    0.04D
            );
        }
    }
}
