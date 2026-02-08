package com.kltyton.mob_battle.entity.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PoisonousBeachEntity extends Entity implements GeoEntity {
    private static final TrackedData<Integer> AGE = DataTracker.registerData(PoisonousBeachEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public PoisonousBeachEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true; // 护盾本身不被物理引擎阻挡
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(AGE, 0);
    }
    @Override
    public void tick() {
        super.tick();
        int age = this.dataTracker.get(AGE);
        this.dataTracker.set(AGE, age + 1);

        if (!getWorld().isClient) {
            if (age > 600) {
                this.discard();
            }
        }
    }
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readCustomData(ReadView view) {
        this.dataTracker.set(AGE, view.getInt("age", 0));
    }

    @Override
    protected void writeCustomData(WriteView view) {
        view.putInt("age", this.dataTracker.get(AGE));
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
