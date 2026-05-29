package com.kltyton.mob_battle.entity.drone.attackdrone;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class AttackDroneEntityModel extends GeoModel<AttackDroneEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "attack_drone");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "attack_drone");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/drone/attack_drone.png");

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(AttackDroneEntity animatable) {
        return animations;
    }
}
