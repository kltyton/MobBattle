package com.kltyton.mob_battle.entity.sugarmanscorpion;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class SugarManScorpionRenderer <R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<SugarManScorpion, R> {
    public SugarManScorpionRenderer(EntityRendererFactory.Context context) {
        super(context, new SugarManScorpionModel());
    }
}
