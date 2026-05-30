package com.kltyton.mob_battle.entity.vindicatorgeneral;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class VindicatorGeneralEntityModel extends GeoModel<VindicatorGeneralEntity> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "vindicator_general");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "vindicator_general");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/vindicator_general/vindicator_general.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(VindicatorGeneralEntity animatable) {
        return animations;
    }
}
