package com.kltyton.mob_battle.block.mushroom;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class MushroomBlockModel extends GeoModel<MushroomBlockEntity> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "mushroom");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "mushroom");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/block/mushroom/mushroom.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(MushroomBlockEntity animatable) {
        return animations;
    }
}