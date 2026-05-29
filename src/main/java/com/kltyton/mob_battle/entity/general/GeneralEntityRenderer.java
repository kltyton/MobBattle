package com.kltyton.mob_battle.entity.general;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class GeneralEntityRenderer<T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    public GeneralEntityRenderer(EntityRendererProvider.Context context, String name, boolean hasHand, GeneralEntityModel.RenderTypes renderType) {
        super(context, new GeneralEntityModel<>(name, hasHand, renderType));
    }
    public GeneralEntityRenderer(EntityRendererProvider.Context context, String name, boolean hasHand) {
        super(context, new GeneralEntityModel<>(name, hasHand, GeneralEntityModel.RenderTypes.CUTOUT));
    }
}
