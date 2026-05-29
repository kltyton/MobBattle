package com.kltyton.mob_battle.entity.xunsheng;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class XunShengEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<XunShengEntity, R> {
    public XunShengEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new XunShengEntityModel());
    }
}
