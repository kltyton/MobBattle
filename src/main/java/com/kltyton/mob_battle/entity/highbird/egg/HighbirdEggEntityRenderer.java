package com.kltyton.mob_battle.entity.highbird.egg;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.jetbrains.annotations.ApiStatus;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class HighbirdEggEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<HighbirdEggEntity, R> {

    public HighbirdEggEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new HighbirdEggEntityModel());
    }
    @ApiStatus.Internal
    @Override
    public void captureDefaultRenderState(HighbirdEggEntity animatable, Void relatedObject, R renderState, float partialTick) {
        super.captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);
        renderState.addGeckolibData(HighbirdEggEntity.STATUS_TICKET, animatable.getStatus());
    }
}
