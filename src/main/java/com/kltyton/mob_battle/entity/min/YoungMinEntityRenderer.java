package com.kltyton.mob_battle.entity.min;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class YoungMinEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<YoungMinEntity, R> {
    public YoungMinEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new YoungMinEntityModel());
    }
    @Nullable
    @Override
    public RenderType getRenderType(R renderState, ResourceLocation texture) {
        return RenderType.entityTranslucentEmissive(texture);
    }
}