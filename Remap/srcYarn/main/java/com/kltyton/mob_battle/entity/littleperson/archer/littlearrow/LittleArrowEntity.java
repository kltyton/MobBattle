package com.kltyton.mob_battle.entity.littleperson.archer.littlearrow;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.bullet.TrueDamageProjectile;
import com.kltyton.mob_battle.entity.littleperson.giant.LittlePersonGiantEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LittleArrowEntity extends TrueDamageProjectile {
    private static final TrackedData<Integer> COLOR = DataTracker.registerData(LittleArrowEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public LittleArrowEntity(EntityType<? extends LittleArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public LittleArrowEntity(World world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(ModEntities.LITTLE_ARROW, x, y, z, world, stack, shotFrom);
        this.initColor();
    }

    public LittleArrowEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(ModEntities.LITTLE_ARROW, owner, world, stack, shotFrom);
        this.initColor();
    }
    public LittleArrowEntity(EntityType<LittleArrowEntity> entityType, World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(entityType, owner, world, stack, shotFrom);
        this.initColor();
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
    public void addEffect(StatusEffectInstance effect) {
        this.setPotionContents(this.getPotionContents().with(effect));
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
            if (this.isInGround()) {
                if (this.inGroundTime % 5 == 0) {
                    this.spawnParticles(1);
                }
            } else {
                this.spawnParticles(2);
            }
        } else if (this.isInGround() && this.inGroundTime != 0 && !this.getPotionContents().equals(PotionContentsComponent.DEFAULT) && this.inGroundTime >= 600) {
            this.getWorld().sendEntityStatus(this, (byte)0);
            this.setStack(new ItemStack(Items.ARROW));
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
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (this.getOwner() instanceof LittlePersonGiantEntity) this.discard();
    }
    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.ARROW);
    }
    @Override
    public void handleStatus(byte status) {
        if (status == 0) {
            int i = this.getColor();
            if (i != -1) {
                float f = (i >> 16 & 0xFF) / 255.0F;
                float g = (i >> 8 & 0xFF) / 255.0F;
                float h = (i & 0xFF) / 255.0F;

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
