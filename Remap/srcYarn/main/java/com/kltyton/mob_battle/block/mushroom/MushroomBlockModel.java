package com.kltyton.mob_battle.block.mushroom;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class MushroomBlockModel extends GeoModel<MushroomBlockEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "mushroom");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "mushroom");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/block/mushroom/mushroom.png");

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