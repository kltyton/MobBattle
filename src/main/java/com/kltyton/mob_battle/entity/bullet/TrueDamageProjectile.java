package com.kltyton.mob_battle.entity.bullet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class TrueDamageProjectile extends PersistentProjectileEntity implements ITrueDamageProjectile {
    boolean TrueDamage = false;
    boolean isMage = false;

    protected TrueDamageProjectile(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    protected TrueDamageProjectile(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(type, owner, world, stack, shotFrom);
    }

    protected TrueDamageProjectile(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z, World world, ItemStack stack, @Nullable ItemStack weapon) {
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
