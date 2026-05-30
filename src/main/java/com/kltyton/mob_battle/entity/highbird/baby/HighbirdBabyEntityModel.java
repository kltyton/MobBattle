package com.kltyton.mob_battle.entity.highbird.baby;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class HighbirdBabyEntityModel extends GeoModel<HighbirdBabyEntity> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_baby");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_baby");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/highbird_baby.png");
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(HighbirdBabyEntity animatable) {
        return animations;
    }
}
