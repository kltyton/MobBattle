package com.kltyton.mob_battle.entity.min;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class YoungMinEntityModel extends GeoModel<YoungMinEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "young_min");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "young_min");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/zz_entity/young_min.png");

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
