package com.kltyton.mob_battle.entity.highbird.adulthood;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class HighbirdAdulthoodEntityModel extends GeoModel<HighbirdAdulthoodEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_adulthood");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_adulthood");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/highbird_adulthood.png");
    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(HighbirdAdulthoodEntity animatable) {
        return animations;
    }
}
