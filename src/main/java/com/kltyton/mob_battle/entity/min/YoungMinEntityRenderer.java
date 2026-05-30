package com.kltyton.mob_battle.entity.min;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class YoungMinEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<YoungMinEntity, R> {
    public YoungMinEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new YoungMinEntityModel());
    }
    @Nullable
    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucentEmissive(texture);
    }
}