package com.kltyton.mob_battle.entity.lobster;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class MagmaLobsterEntityModel extends GeoModel<LobsterEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "magma_lobster_entity");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "magma_lobster_entity");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/lobster_entity/magma_lobster_entity.png");

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(LobsterEntity animatable) {
        return animations;
    }
}