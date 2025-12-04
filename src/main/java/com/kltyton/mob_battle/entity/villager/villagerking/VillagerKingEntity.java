package com.kltyton.mob_battle.entity.villager.villagerking;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VillagerKingEntity extends MobEntity implements GeoItem {
    public VillagerKingEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        this.setAiDisabled(true);
    }
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDEA_ANIM = RawAnimation.begin().thenLoop("idle");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main_controller", t -> t.setAndContinue(IDEA_ANIM)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    public static DefaultAttributeContainer.Builder createVillagerKingAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.MAX_HEALTH, 9999);
    }
    @Override
    public void takeKnockback(double strength, double x, double z) {
    }
}
