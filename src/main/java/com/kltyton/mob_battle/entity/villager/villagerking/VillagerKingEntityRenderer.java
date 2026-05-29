package com.kltyton.mob_battle.entity.villager.villagerking;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VillagerKingEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<VillagerKingEntity, R> {
    public VillagerKingEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerKingEntityModel());
    }
}