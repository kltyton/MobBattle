package com.kltyton.mob_battle.entity.general;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.Entity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class GeneralEntityRenderer<T extends Entity & GeoAnimatable, R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    public GeneralEntityRenderer(EntityRendererFactory.Context context, String name, boolean hasHand) {
        super(context, new GeneralEntityModel<>(name, hasHand));
    }
}
