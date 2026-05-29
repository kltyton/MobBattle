package com.kltyton.mob_battle.entity.bullet;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.effect.harmful.IceEffect;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class IceArrowEntity extends TrueDamageProjectile {
    private boolean iceTipped = false;

    public IceArrowEntity(EntityType<? extends IceArrowEntity> entityType, Level world) {
        super(entityType, world);
        this.pickup = Pickup.DISALLOWED;
    }

    public IceArrowEntity(Level world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(ModEntities.ICE_ARROW, owner, world, stack, shotFrom);
        this.pickup = Pickup.DISALLOWED;
    }

    public IceArrowEntity(Level world, double x, double y, double z, ItemStack stack, @Nullable ItemStack weapon) {
        super(ModEntities.ICE_ARROW, x, y, z, world, stack, weapon);
        this.pickup = Pickup.DISALLOWED;
    }

    public void setIceTipped(boolean iceTipped) {
        this.iceTipped = iceTipped;
    }

    public boolean isIceTipped() {
        return this.iceTipped;
    }

    @Override
    public void additionalDamage(Entity entity) {
        if (iceTipped) entity.hurtOrSimulate(this.damageSources().indirectMagic(this, this.getOwner() != null ? this.getOwner() : this), 30.0f);
    }
    @Override
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        this.pickup = Pickup.DISALLOWED;
    }
    @Override
    protected boolean tryPickup(Player player) {
        return false;
    }

    @Override
    protected void doPostHurtEffects(LivingEntity target) {
        super.doPostHurtEffects(target);

        if (!this.iceTipped) {
            return;
        }

        Entity effectCause = this.getEffectSource();
        MobEffectInstance current = target.getEffect(ModEffects.ICE_ENTRY);

        if (current == null) {
            target.addEffect(
                    new MobEffectInstance(ModEffects.ICE_ENTRY, IceEffect.DEFAULT_DURATION, 0),
                    effectCause
            );
            return;
        }

        int newAmplifier = Math.min(current.getAmplifier() + 1, IceEffect.MAX_LEVEL);
        int newDuration = Math.max(current.getDuration(), IceEffect.DEFAULT_DURATION);

        target.addEffect(
                new MobEffectInstance(
                        ModEffects.ICE_ENTRY,
                        newDuration,
                        newAmplifier,
                        current.isAmbient(),
                        current.isVisible(),
                        current.showIcon()
                ),
                effectCause
        );
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.ICE_ARROW_ITEM);
    }
}