package com.kltyton.mob_battle.entity.littleperson.civilian;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class LittlePersonWorkerEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LittlePersonWorkerEntity, R> {
    public LittlePersonWorkerEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new LittlePersonWorkerEntityModel());
    }
}
