package com.kltyton.mob_battle.entity.villager.villagerking;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VillagerKingEntity extends Mob implements GeoItem {
    public VillagerKingEntity(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
        this.setNoAi(true);
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
    public static AttributeSupplier.Builder createVillagerKingAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 9999);
    }
    @Override
    public void knockback(double strength, double x, double z) {
    }
}
