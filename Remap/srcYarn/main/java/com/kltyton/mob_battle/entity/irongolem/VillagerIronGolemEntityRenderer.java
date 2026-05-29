package com.kltyton.mob_battle.entity.irongolem;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VillagerIronGolemEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<VillagerIronGolemEntity, R> {
    public VillagerIronGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new VillagerIronGolemEntityModel());
    }
}
