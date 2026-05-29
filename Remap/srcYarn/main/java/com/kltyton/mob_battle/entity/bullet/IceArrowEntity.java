package com.kltyton.mob_battle.entity.bullet;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.effect.harmful.IceEffect;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class IceArrowEntity extends TrueDamageProjectile {
    private boolean iceTipped = false;

    public IceArrowEntity(EntityType<? extends IceArrowEntity> entityType, World world) {
        super(entityType, world);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public IceArrowEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(ModEntities.ICE_ARROW, owner, world, stack, shotFrom);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public IceArrowEntity(World world, double x, double y, double z, ItemStack stack, @Nullable ItemStack weapon) {
        super(ModEntities.ICE_ARROW, x, y, z, world, stack, weapon);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public void setIceTipped(boolean iceTipped) {
        this.iceTipped = iceTipped;
    }

    public boolean isIceTipped() {
        return this.iceTipped;
    }

    @Override
    public void additionalDamage(Entity entity) {
        if (iceTipped) entity.sidedDamage(this.getDamageSources().indirectMagic(this, this.getOwner() != null ? this.getOwner() : this), 30.0f);
    }
    @Override
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        this.pickupType = PickupPermission.DISALLOWED;
    }
    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return false;
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);

        if (!this.iceTipped) {
            return;
        }

        Entity effectCause = this.getEffectCause();
        StatusEffectInstance current = target.getStatusEffect(ModEffects.ICE_ENTRY);

        if (current == null) {
            target.addStatusEffect(
                    new StatusEffectInstance(ModEffects.ICE_ENTRY, IceEffect.DEFAULT_DURATION, 0),
                    effectCause
            );
            return;
        }

        int newAmplifier = Math.min(current.getAmplifier() + 1, IceEffect.MAX_LEVEL);
        int newDuration = Math.max(current.getDuration(), IceEffect.DEFAULT_DURATION);

        target.addStatusEffect(
                new StatusEffectInstance(
                        ModEffects.ICE_ENTRY,
                        newDuration,
                        newAmplifier,
                        current.isAmbient(),
                        current.shouldShowParticles(),
                        current.shouldShowIcon()
                ),
                effectCause
        );
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.ICE_ARROW_ITEM);
    }
}