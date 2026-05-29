package com.kltyton.mob_battle.entity.highbird.adulthood;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class HighbirdAdulthoodEntityModel extends GeoModel<HighbirdAdulthoodEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "highbird_adulthood");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "highbird_adulthood");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/highbird_adulthood.png");
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
