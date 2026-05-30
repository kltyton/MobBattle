package com.kltyton.mob_battle.entity.sugarmanscorpion;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class SugarManScorpionRenderer <R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<SugarManScorpion, R> {
    public SugarManScorpionRenderer(EntityRendererProvider.Context context) {
        super(context, new SugarManScorpionModel());
    }
}
