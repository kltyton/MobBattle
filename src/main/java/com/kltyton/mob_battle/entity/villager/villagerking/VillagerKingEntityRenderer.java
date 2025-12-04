package com.kltyton.mob_battle.entity.villager.villagerking;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VillagerKingEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<VillagerKingEntity, R> {
    public VillagerKingEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new VillagerKingEntityModel());
    }
}