package com.kltyton.mob_battle.entity.highbird.baby;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class HighbirdBabyEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<HighbirdBabyEntity, R> {
    public HighbirdBabyEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new HighbirdBabyEntityModel());
    }
}
