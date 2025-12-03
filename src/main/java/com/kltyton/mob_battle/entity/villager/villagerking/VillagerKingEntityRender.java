package com.kltyton.mob_battle.entity.villager.villagerking;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VillagerKingEntityRender<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<VillagerKingEntity, R> {
    public VillagerKingEntityRender(EntityRendererFactory.Context context) {
        super(context, new VillagerKingEntityModel());
    }
}