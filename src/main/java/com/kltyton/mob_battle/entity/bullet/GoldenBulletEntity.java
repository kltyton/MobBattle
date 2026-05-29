package com.kltyton.mob_battle.entity.bullet;

import com.kltyton.mob_battle.entity.ModEntities;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public class GoldenBulletEntity extends TrueDamageProjectile {
    private static final EntityDataAccessor<ItemStack> DISPLAY_STACK =
            SynchedEntityData.defineId(GoldenBulletEntity.class, EntityDataSerializers.ITEM_STACK);

    public GoldenBulletEntity(EntityType<GoldenBulletEntity> entityType, Level world) {
        super(entityType, world);
        this.pickup = AbstractArrow.Pickup.DISALLOWED;
    }

    public GoldenBulletEntity(Level world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(ModEntities.GOLDEN_BULLET, owner, world, stack, shotFrom);
        this.pickup = AbstractArrow.Pickup.DISALLOWED;
        this.setDisplayStack(stack);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DISPLAY_STACK, new ItemStack(Items.GOLD_NUGGET));
    }

    @Override
    protected void setPickupItemStack(ItemStack stack) {
        super.setPickupItemStack(stack);
        this.setDisplayStack(stack);
    }

    public ItemStack getDisplayStack() {
        return this.entityData.get(DISPLAY_STACK);
    }

    private void setDisplayStack(ItemStack stack) {
        ItemStack displayStack = stack.copy();
        displayStack.setCount(1);
        this.entityData.set(DISPLAY_STACK, displayStack);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.tickCount > 100) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.GOLD_NUGGET);
    }
}
