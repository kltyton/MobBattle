package com.kltyton.mob_battle.entity.witherskeletonking;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class WitherSkeletonKingRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<WitherSkeletonKingEntity, R> {
    public WitherSkeletonKingRenderer(EntityRendererFactory.Context context) {
        super(context, new WitherSkeletonKingEntityModel());
    }
}
