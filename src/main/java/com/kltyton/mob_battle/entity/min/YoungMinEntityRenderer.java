package com.kltyton.mob_battle.entity.min;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class YoungMinEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<YoungMinEntity, R> {
    public YoungMinEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new YoungMinEntityModel());
    }
    @Nullable
    @Override
    public RenderLayer getRenderType(R renderState, Identifier texture) {
        return RenderLayer.getEntityTranslucentEmissive(texture);
    }
}
