package com.kltyton.mob_battle.entity.littleperson.archer.littlearrow;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.bullet.TrueDamageProjectile;
import com.kltyton.mob_battle.entity.littleperson.giant.LittlePersonGiantEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class LittleArrowEntity extends TrueDamageProjectile {
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(LittleArrowEntity.class, EntityDataSerializers.INT);

    public LittleArrowEntity(EntityType<? extends LittleArrowEntity> entityType, Level world) {
        super(entityType, world);
    }

    public LittleArrowEntity(Level world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(ModEntities.LITTLE_ARROW, x, y, z, world, stack, shotFrom);
        this.initColor();
    }

    public LittleArrowEntity(Level world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(ModEntities.LITTLE_ARROW, owner, world, stack, shotFrom);
        this.initColor();
    }
    public LittleArrowEntity(EntityType<LittleArrowEntity> entityType, Level world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(entityType, owner, world, stack, shotFrom);
        this.initColor();
    }
    private PotionContents getPotionContents() {
        return this.getPickupItemStackOrigin().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    }
    private float getPotionDurationScale() {
        return this.getPickupItemStackOrigin().getOrDefault(DataComponents.POTION_DURATION_SCALE, 1.0F);
    }
    private void setPotionContents(PotionContents potionContentsComponent) {
        this.getPickupItemStackOrigin().set(DataComponents.POTION_CONTENTS, potionContentsComponent);
        this.initColor();
    }
    @Override
    protected void setPickupItemStack(ItemStack stack) {
        super.setPickupItemStack(stack);
        this.initColor();
    }
    private void initColor() {
        PotionContents potionContentsComponent = this.getPotionContents();
        this.entityData.set(COLOR, potionContentsComponent.equals(PotionContents.EMPTY) ? -1 : potionContentsComponent.getColor());
    }
    public void addEffect(MobEffectInstance effect) {
        this.setPotionContents(this.getPotionContents().withEffectAdded(effect));
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COLOR, -1);
    }
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.isInGround()) {
                if (this.inGroundTime % 5 == 0) {
                    this.spawnParticles(1);
                }
            } else {
                this.spawnParticles(2);
            }
        } else if (this.isInGround() && this.inGroundTime != 0 && !this.getPotionContents().equals(PotionContents.EMPTY) && this.inGroundTime >= 600) {
            this.level().broadcastEntityEvent(this, (byte)0);
            this.setPickupItemStack(new ItemStack(Items.ARROW));
        }
    }
    private void spawnParticles(int amount) {
        int i = this.getColor();
        if (i != -1 && amount > 0) {
            for (int j = 0; j < amount; j++) {
                this.level()
                        .addParticle(
                                ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, i), this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0
                        );
            }
        }
    }
    public int getColor() {
        return this.entityData.get(COLOR);
    }
    @Override
    protected void doPostHurtEffects(LivingEntity target) {
        super.doPostHurtEffects(target);
        Entity entity = this.getEffectSource();
        PotionContents potionContentsComponent = this.getPotionContents();
        float f = this.getPotionDurationScale();
        potionContentsComponent.forEachEffect(effect -> target.addEffect(effect, entity), f);
    }
    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.getOwner() instanceof LittlePersonGiantEntity) this.discard();
    }
    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.ARROW);
    }
    @Override
    public void handleEntityEvent(byte status) {
        if (status == 0) {
            int i = this.getColor();
            if (i != -1) {
                float f = (i >> 16 & 0xFF) / 255.0F;
                float g = (i >> 8 & 0xFF) / 255.0F;
                float h = (i & 0xFF) / 255.0F;

                for (int j = 0; j < 20; j++) {
                    this.level()
                            .addParticle(
                                    ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, f, g, h), this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0
                            );
                }
            }
        } else {
            super.handleEntityEvent(status);
        }
    }
}
