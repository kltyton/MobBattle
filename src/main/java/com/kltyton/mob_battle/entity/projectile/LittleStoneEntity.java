package com.kltyton.mob_battle.entity.projectile;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class LittleStoneEntity extends ThrowableItemProjectile {
    public LittleStoneEntity(EntityType<? extends LittleStoneEntity> entityType, Level level) {
        super(entityType, level);
    }

    public LittleStoneEntity(Level level, LivingEntity owner, ItemStack stack) {
        super(ModEntities.LITTLE_STONE_PROJECTILE, owner, level, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.LITTLE_STONE;
    }

    private ParticleOptions getParticle() {
        ItemStack stack = this.getItem();
        return stack.isEmpty()
                ? new ItemParticleOption(ParticleTypes.ITEM, ModItems.LITTLE_STONE)
                : new ItemParticleOption(ParticleTypes.ITEM, stack.getItem());
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 3) {
            ParticleOptions particle = this.getParticle();
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity target = hitResult.getEntity();
        if (this.level() instanceof ServerLevel world) {
            target.hurtServer(world, this.damageSources().thrown(this, this.getOwner()), 1.0F);
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }
}
