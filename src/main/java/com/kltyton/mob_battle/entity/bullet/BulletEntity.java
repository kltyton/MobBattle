package com.kltyton.mob_battle.entity.bullet;

import com.kltyton.mob_battle.entity.ModEntities;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BulletEntity extends PersistentProjectileEntity {
    private static final TrackedData<Integer> COLOR = DataTracker.registerData(BulletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public boolean Fixed_damage = false;
    public BulletEntity(EntityType<BulletEntity> entityType, World world) {
        super(entityType, world);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public BulletEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(ModEntities.BULLET_ENTITY, owner, world, stack, shotFrom);
        this.initColor();
        this.pickupType = PickupPermission.DISALLOWED;
    }


    private PotionContentsComponent getPotionContents() {
        return this.getItemStack().getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
    }

    private float getPotionDurationScale() {
        return this.getItemStack().getOrDefault(DataComponentTypes.POTION_DURATION_SCALE, 1.0F);
    }

    private void setPotionContents(PotionContentsComponent potionContentsComponent) {
        this.getItemStack().set(DataComponentTypes.POTION_CONTENTS, potionContentsComponent);
        this.initColor();
    }

    @Override
    protected void setStack(ItemStack stack) {
        super.setStack(stack);
        this.initColor();
    }

    private void initColor() {
        PotionContentsComponent potionContentsComponent = this.getPotionContents();
        this.dataTracker.set(COLOR, potionContentsComponent.equals(PotionContentsComponent.DEFAULT) ? -1 : potionContentsComponent.getColor());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(COLOR, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            this.pickupType = PickupPermission.DISALLOWED;
            if (this.isInGround()) {
                if (this.inGroundTime % 5 == 0) {
                    this.spawnParticles(1);
                }
            } else {
                this.spawnParticles(2);
            }
        } else if (this.isInGround() && this.inGroundTime != 0 && !this.getPotionContents().equals(PotionContentsComponent.DEFAULT) && this.inGroundTime >= 600) {
            this.getWorld().sendEntityStatus(this, (byte)0);
            this.setStack(new ItemStack(Items.IRON_BLOCK));
        }
    }

    private void spawnParticles(int amount) {
        int i = this.getColor();
        if (i != -1 && amount > 0) {
            for (int j = 0; j < amount; j++) {
                this.getWorld()
                        .addParticleClient(
                                TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, i), this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 0.0, 0.0, 0.0
                        );
            }
        }
    }

    public int getColor() {
        return this.dataTracker.get(COLOR);
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);
        Entity entity = this.getEffectCause();
        PotionContentsComponent potionContentsComponent = this.getPotionContents();
        float f = this.getPotionDurationScale();
        potionContentsComponent.forEachEffect(effect -> target.addStatusEffect(effect, entity), f);
    }
    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.IRON_BLOCK);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 0) {
            int i = this.getColor();
            if (i != -1) {
                float f = (i >> 16 & 0xFF) / 255.0F;
                float g = (i >> 8 & 0xFF) / 255.0F;
                float h = (i >> 0 & 0xFF) / 255.0F;

                for (int j = 0; j < 20; j++) {
                    this.getWorld()
                            .addParticleClient(
                                    TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, f, g, h), this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 0.0, 0.0, 0.0
                            );
                }
            }
        } else {
            super.handleStatus(status);
        }
    }
}
