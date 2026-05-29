package com.kltyton.mob_battle.block.nest;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class NestBlockModel extends GeoModel<NestBlockEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "nest");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "nest");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/block/nest/nest.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(NestBlockEntity animatable) {
        return animations;
    }
}