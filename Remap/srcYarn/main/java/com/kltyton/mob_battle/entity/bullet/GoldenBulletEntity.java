package com.kltyton.mob_battle.entity.bullet;

import com.kltyton.mob_battle.entity.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GoldenBulletEntity extends TrueDamageProjectile {
    private static final TrackedData<ItemStack> DISPLAY_STACK =
            DataTracker.registerData(GoldenBulletEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    public GoldenBulletEntity(EntityType<GoldenBulletEntity> entityType, World world) {
        super(entityType, world);
        this.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
    }

    public GoldenBulletEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(ModEntities.GOLDEN_BULLET, owner, world, stack, shotFrom);
        this.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
        this.setDisplayStack(stack);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(DISPLAY_STACK, new ItemStack(Items.GOLD_NUGGET));
    }

    @Override
    protected void setStack(ItemStack stack) {
        super.setStack(stack);
        this.setDisplayStack(stack);
    }

    public ItemStack getDisplayStack() {
        return this.dataTracker.get(DISPLAY_STACK);
    }

    private void setDisplayStack(ItemStack stack) {
        ItemStack displayStack = stack.copy();
        displayStack.setCount(1);
        this.dataTracker.set(DISPLAY_STACK, displayStack);
    }

    @Override
    protected double getGravity() {
        return 0.0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient() && this.age > 100) {
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!this.getWorld().isClient()) {
            this.discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.getWorld().isClient()) {
            this.discard();
        }
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.GOLD_NUGGET);
    }
}
