package com.kltyton.mob_battle.entity.littleperson.archer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class LittlePersonArcherEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LittlePersonArcherEntity, R> {
    public LittlePersonArcherEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new LittlePersonArcherEntityModel());
    }
}
