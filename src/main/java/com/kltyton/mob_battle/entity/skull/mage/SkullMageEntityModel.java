package com.kltyton.mob_battle.entity.skull.mage;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class SkullMageEntityModel extends GeoModel<SkullMageEntity> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "skull_mage");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "skull_mage");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/skull/skull_mage.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(SkullMageEntity animatable) {
        return animations;
    }
}
