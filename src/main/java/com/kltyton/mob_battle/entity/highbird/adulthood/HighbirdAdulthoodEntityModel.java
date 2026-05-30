package com.kltyton.mob_battle.entity.highbird.adulthood;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class HighbirdAdulthoodEntityModel extends GeoModel<HighbirdAdulthoodEntity> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_adulthood");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_adulthood");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/highbird_adulthood.png");
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(HighbirdAdulthoodEntity animatable) {
        return animations;
    }
}
