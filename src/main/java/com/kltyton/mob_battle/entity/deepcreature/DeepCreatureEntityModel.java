package com.kltyton.mob_battle.entity.deepcreature;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DeepCreatureEntityModel extends GeoModel<DeepCreatureEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "deep_creature");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "deep_creature");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/deep_creature/deep_creature.png");

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(DeepCreatureEntity animatable) {
        return animations;
    }
}
