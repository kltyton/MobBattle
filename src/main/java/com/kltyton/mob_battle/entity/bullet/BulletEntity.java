package com.kltyton.mob_battle.entity.bullet;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.items.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BulletEntity extends TrueDamageProjectile {
    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(BulletEntity.class, EntityDataSerializers.INT);

    public BulletEntity(EntityType<BulletEntity> entityType, Level world) {
        super(entityType, world);
        this.pickup = Pickup.DISALLOWED;
    }

    public BulletEntity(Level world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(ModEntities.BULLET_ENTITY, owner, world, stack, shotFrom);
        this.initColor();
        this.pickup = Pickup.DISALLOWED;
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
        this.entityData.set(COLOR,
                potionContentsComponent.equals(PotionContents.EMPTY) ? -1 : potionContentsComponent.getColor());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COLOR, -1);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }

    @Override
    public void tick() {
        double prevX = this.getX();
        double prevY = this.getY();
        double prevZ = this.getZ();

        super.tick();

        if (this.level().isClientSide) {
            this.pickup = Pickup.DISALLOWED;

            if (this.isInGround()) {
                if (this.inGroundTime % 5 == 0) {
                    this.spawnInGroundParticles(1);
                }
            } else {
                this.spawnTrailParticles(prevX, prevY, prevZ);
            }
        } else if (this.isInGround()
                && this.inGroundTime != 0
                && !this.getPotionContents().equals(PotionContents.EMPTY)
                && this.inGroundTime >= 600) {
            this.level().broadcastEntityEvent(this, (byte) 0);
            this.setPickupItemStack(new ItemStack(ModItems.COMPRESSED_IRON_INGOT));
        }
    }

    /**
     * 飞行中的尾迹粒子。
     * 使用“上一帧位置 -> 当前帧位置”插值生成，保证尾迹严格贴着弹道。
     */
    private void spawnTrailParticles(double prevX, double prevY, double prevZ) {
        int color = this.getColor();

        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        // 每 tick 生成的尾迹密度，可自行调大/调小
        int count = 4;

        for (int i = 0; i < count; i++) {
            double t = i / (double) count;
            double px = Mth.lerp(t, prevX, x);
            double py = Mth.lerp(t, prevY, y);
            double pz = Mth.lerp(t, prevZ, z);

            if (color != -1) {
                this.level().addParticle(
                        ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color),
                        px, py, pz,
                        0.0, 0.0, 0.0
                );
            } else {
                this.level().addParticle(
                        ParticleTypes.CRIT,
                        px, py, pz,
                        0.0, 0.0, 0.0
                );
            }
        }
    }

    /**
     * 子弹落地后停留时的粒子。
     */
    private void spawnInGroundParticles(int amount) {
        int color = this.getColor();
        if (color != -1 && amount > 0) {
            for (int i = 0; i < amount; i++) {
                this.level().addParticle(
                        ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color),
                        this.getX(),
                        this.getY() + 0.05,
                        this.getZ(),
                        0.0, 0.0, 0.0
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
        float durationScale = this.getPotionDurationScale();
        potionContentsComponent.forEachEffect(effect -> target.addEffect(effect, entity), durationScale);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.COMPRESSED_IRON_INGOT);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 0) {
            int color = this.getColor();
            if (color != -1) {
                float r = (color >> 16 & 0xFF) / 255.0F;
                float g = (color >> 8 & 0xFF) / 255.0F;
                float b = (color & 0xFF) / 255.0F;

                for (int i = 0; i < 20; i++) {
                    this.level().addParticle(
                            ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, r, g, b),
                            this.getX(),
                            this.getY() + 0.05,
                            this.getZ(),
                            0.0, 0.0, 0.0
                    );
                }
            }
        } else {
            super.handleEntityEvent(status);
        }
    }
}
