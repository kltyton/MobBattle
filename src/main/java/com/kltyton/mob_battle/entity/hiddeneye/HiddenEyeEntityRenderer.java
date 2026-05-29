package com.kltyton.mob_battle.entity.hiddeneye;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class HiddenEyeEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<HiddenEyeEntity, R> {
    public HiddenEyeEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new HiddenEyeEntityModel());
    }
}
