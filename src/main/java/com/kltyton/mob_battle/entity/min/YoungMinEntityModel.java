package com.kltyton.mob_battle.entity.min;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class YoungMinEntityModel extends GeoModel<YoungMinEntity> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "young_min");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "young_min");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/zz_entity/young_min.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(YoungMinEntity animatable) {
        return animations;
    }
}
