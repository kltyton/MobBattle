package com.kltyton.mob_battle.entity.littleperson.giant;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class LittlePersonGiantEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LittlePersonGiantEntity, R> {
    public LittlePersonGiantEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new LittlePersonGiantEntityModel());
    }
}

