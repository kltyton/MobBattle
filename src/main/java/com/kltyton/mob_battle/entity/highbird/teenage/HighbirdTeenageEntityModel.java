package com.kltyton.mob_battle.entity.highbird.teenage;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class HighbirdTeenageEntityModel extends GeoModel<HighbirdTeenageEntity> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_teenage");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "highbird_teenage");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/highbird_teenage.png");
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(HighbirdTeenageEntity animatable) {
        return animations;
    }
}
