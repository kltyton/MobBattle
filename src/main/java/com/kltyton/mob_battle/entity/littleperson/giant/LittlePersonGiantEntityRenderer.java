package com.kltyton.mob_battle.entity.littleperson.giant;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class LittlePersonGiantEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LittlePersonGiantEntity, R> {
    public LittlePersonGiantEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new LittlePersonGiantEntityModel());
    }
}

