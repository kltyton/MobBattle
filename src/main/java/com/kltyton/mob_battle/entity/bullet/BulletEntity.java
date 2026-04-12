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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BulletEntity extends TrueDamageProjectile {
    private static final TrackedData<Integer> COLOR =
            DataTracker.registerData(BulletEntity.class, TrackedDataHandlerRegistry.INTEGER);

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
        this.dataTracker.set(COLOR,
                potionContentsComponent.equals(PotionContentsComponent.DEFAULT) ? -1 : potionContentsComponent.getColor());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(COLOR, -1);
    }

    @Override
    protected double getGravity() {
        return 0.0;
    }

    @Override
    public void tick() {
        double prevX = this.getX();
        double prevY = this.getY();
        double prevZ = this.getZ();

        super.tick();

        if (this.getWorld().isClient) {
            this.pickupType = PickupPermission.DISALLOWED;

            if (this.isInGround()) {
                if (this.inGroundTime % 5 == 0) {
                    this.spawnInGroundParticles(1);
                }
            } else {
                this.spawnTrailParticles(prevX, prevY, prevZ);
            }
        } else if (this.isInGround()
                && this.inGroundTime != 0
                && !this.getPotionContents().equals(PotionContentsComponent.DEFAULT)
                && this.inGroundTime >= 600) {
            this.getWorld().sendEntityStatus(this, (byte) 0);
            this.setStack(new ItemStack(Items.IRON_BLOCK));
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
            double px = MathHelper.lerp(t, prevX, x);
            double py = MathHelper.lerp(t, prevY, y);
            double pz = MathHelper.lerp(t, prevZ, z);

            if (color != -1) {
                this.getWorld().addParticleClient(
                        TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, color),
                        px, py, pz,
                        0.0, 0.0, 0.0
                );
            } else {
                this.getWorld().addParticleClient(
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
                this.getWorld().addParticleClient(
                        TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, color),
                        this.getX(),
                        this.getY() + 0.05,
                        this.getZ(),
                        0.0, 0.0, 0.0
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
        float durationScale = this.getPotionDurationScale();
        potionContentsComponent.forEachEffect(effect -> target.addStatusEffect(effect, entity), durationScale);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.IRON_BLOCK);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 0) {
            int color = this.getColor();
            if (color != -1) {
                float r = (color >> 16 & 0xFF) / 255.0F;
                float g = (color >> 8 & 0xFF) / 255.0F;
                float b = (color & 0xFF) / 255.0F;

                for (int i = 0; i < 20; i++) {
                    this.getWorld().addParticleClient(
                            TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b),
                            this.getX(),
                            this.getY() + 0.05,
                            this.getZ(),
                            0.0, 0.0, 0.0
                    );
                }
            }
        } else {
            super.handleStatus(status);
        }
    }
}
