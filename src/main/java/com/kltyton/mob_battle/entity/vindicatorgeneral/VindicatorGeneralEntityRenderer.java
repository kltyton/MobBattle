package com.kltyton.mob_battle.entity.vindicatorgeneral;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VindicatorGeneralEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<VindicatorGeneralEntity, R> {

    public VindicatorGeneralEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new VindicatorGeneralEntityModel());
    }
}