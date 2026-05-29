package com.kltyton.mob_battle.entity.misc;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PoisonousBeachEntity extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(PoisonousBeachEntity.class, EntityDataSerializers.INT);

    public PoisonousBeachEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.noPhysics = true; // 护盾本身不被物理引擎阻挡
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(AGE, 0);
    }
    @Override
    public void tick() {
        super.tick();
        int age = this.entityData.get(AGE);
        this.entityData.set(AGE, age + 1);

        if (!level().isClientSide) {
            if (age > 600) {
                this.discard();
            }
        }
    }
    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        this.entityData.set(AGE, view.getIntOr("age", 0));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        view.putInt("age", this.entityData.get(AGE));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
