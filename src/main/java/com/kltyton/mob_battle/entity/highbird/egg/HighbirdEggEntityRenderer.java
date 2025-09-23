package com.kltyton.mob_battle.entity.highbird.egg;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class HighbirdEggEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<HighbirdEggEntity, R> {

    public HighbirdEggEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new HighbirdEggEntityModel());
    }
    @ApiStatus.Internal
    @Override
    public R captureDefaultRenderState(HighbirdEggEntity animatable, Void relatedObject, R renderState, float partialTick) {
        super.captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);
        renderState.addGeckolibData(HighbirdEggEntity.STATUS_TICKET, animatable.getStatus());
        return renderState;
    }
}
