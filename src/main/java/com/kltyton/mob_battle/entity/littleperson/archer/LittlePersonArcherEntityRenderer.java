package com.kltyton.mob_battle.entity.littleperson.archer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class LittlePersonArcherEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LittlePersonArcherEntity, R> {
    public LittlePersonArcherEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new LittlePersonArcherEntityModel());
    }
}
