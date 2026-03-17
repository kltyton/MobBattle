package com.kltyton.mob_battle.entity.lobster;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class MagmaLobsterEntityModel extends GeoModel<LobsterEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "magma_lobster_entity");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "magma_lobster_entity");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/lobster_entity/magma_lobster_entity.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(LobsterEntity animatable) {
        return animations;
    }
}