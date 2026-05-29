package com.kltyton.mob_battle.entity.lobster;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class MagmaLobsterEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LobsterEntity, R> {
    public MagmaLobsterEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new MagmaLobsterEntityModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}