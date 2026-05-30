package com.kltyton.mob_battle.entity.hiddeneye;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class HiddenEyeEntityModel extends GeoModel<HiddenEyeEntity> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hidden_eye");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "hidden_eye");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/zz_entity/hidden_eye.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(HiddenEyeEntity animatable) {
        return animations;
    }
}
