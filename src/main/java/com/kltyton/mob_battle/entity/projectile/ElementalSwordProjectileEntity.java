package com.kltyton.mob_battle.entity.projectile;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntities;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ElementalSwordProjectileEntity extends ThrowableItemProjectile {
    public static final int ICE_SWORD = 0;
    public static final int FIRE_SWORD = 1;
    public static final int WIND_ICE = 2;
    public static final int WIND_MAGMA = 3;

    private int mode = ICE_SWORD;

    public ElementalSwordProjectileEntity(EntityType<? extends ElementalSwordProjectileEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ElementalSwordProjectileEntity(Level level, LivingEntity owner, ItemStack stack, int mode) {
        super(ModEntities.ELEMENTAL_SWORD_PROJECTILE, owner, level, stack);
        this.mode = mode;
    }

    @Override
    protected Item getDefaultItem() {
        return switch (mode) {
            case FIRE_SWORD -> Items.FIRE_CHARGE;
            case WIND_ICE -> Items.ICE;
            case WIND_MAGMA -> Items.MAGMA_BLOCK;
            default -> Items.SNOWBALL;
        };
    }

    private ParticleOptions getParticle() {
        ItemStack stack = this.getItem();
        return stack.isEmpty()
                ? new ItemParticleOption(ParticleTypes.ITEM, getDefaultItem())
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
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        Entity owner = this.getOwner();
        if (owner != null && owner.isAlliedTo(target)) {
            return;
        }

        Vec3 previousMotion = target.getDeltaMovement();
        target.invulnerableTime = 0;
        target.hurtServer(world, this.damageSources().thrown(this, owner), damage());
        target.invulnerableTime = 0;
        if (target instanceof LivingEntity living) {
            applyEffect(living);
            if (mode == ICE_SWORD || mode == FIRE_SWORD) {
                living.setDeltaMovement(previousMotion);
            } else {
                living.knockback(5.0D, this.getX() - living.getX(), this.getZ() - living.getZ());
            }
        }
    }

    private float damage() {
        return switch (mode) {
            case FIRE_SWORD -> 2.0F;
            case WIND_ICE -> 35.0F;
            case WIND_MAGMA -> 45.0F;
            default -> 1.5F;
        };
    }

    private void applyEffect(LivingEntity target) {
        switch (mode) {
            case ICE_SWORD -> target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 3 * 20, 0), getEffectSource());
            case FIRE_SWORD -> target.igniteForSeconds(5);
            case WIND_ICE -> {
                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 3 * 20, 0), getEffectSource());
                target.addEffect(new MobEffectInstance(ModEffects.ICE_ENTRY, 2 * 20, 0), getEffectSource());
            }
            case WIND_MAGMA -> {
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 3 * 20, 2), getEffectSource());
                target.igniteForSeconds(15);
            }
            default -> {
            }
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

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Mode", mode);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.mode = input.getIntOr("Mode", ICE_SWORD);
    }
}
