package com.kltyton.mob_battle.entity.skull.king;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class SkullKingEntityRenderer <R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<SkullKingEntity, R> {
    public SkullKingEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new SkullKingEntityModel());
    }
}
