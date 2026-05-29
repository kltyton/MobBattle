package com.kltyton.mob_battle.entity.bullet;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class TrueDamageProjectile extends AbstractArrow implements ITrueDamageProjectile {
    boolean TrueDamage = false;
    boolean isMage = false;
    protected TrueDamageProjectile(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }
    protected TrueDamageProjectile(EntityType<? extends AbstractArrow> type, LivingEntity owner, Level world, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(type, owner, world, stack, shotFrom);
    }

    protected TrueDamageProjectile(EntityType<? extends AbstractArrow> type, double x, double y, double z, Level world, ItemStack stack, @Nullable ItemStack weapon) {
        super(type, x, y, z, world, stack, weapon);
    }
    @Override
    public void setTrueDamage(boolean fixed_damage, Boolean isMage) {
        TrueDamage = fixed_damage;
        this.isMage = isMage;
    }
    @Override
    public boolean isTrueDamage() {
        return TrueDamage;
    }
    @Override
    public boolean isMage() {
        return isMage;
    }
}
