package com.kltyton.mob_battle.entity.skull.archer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class SkullArcherEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<SkullArcherEntity, R> {
    public SkullArcherEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new SkullArcherEntityModel());
    }
}
