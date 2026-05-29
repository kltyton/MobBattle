package com.kltyton.mob_battle.entity.sugarmanscorpion;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class SugarManScorpionModel extends GeoModel<SugarManScorpion> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "sugar_man_scorpion");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "sugar_man_scorpion");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/sugar_man_scorpion.png");
    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SugarManScorpion animatable) {
        return this.animations;
    }
}
