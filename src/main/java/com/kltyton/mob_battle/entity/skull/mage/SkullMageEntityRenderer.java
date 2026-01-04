package com.kltyton.mob_battle.entity.skull.mage;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class SkullMageEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<SkullMageEntity, R> {
    public SkullMageEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SkullMageEntityModel());
    }
}
