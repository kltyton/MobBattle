package com.kltyton.mob_battle.entity.lobster;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;

public class MagmaLobsterEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LobsterEntity, R> {
    public MagmaLobsterEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new MagmaLobsterEntityModel());
        this.withRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}