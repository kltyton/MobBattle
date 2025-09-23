package com.kltyton.mob_battle.entity.villager.archervillager;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class ArcherVillagerRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<ArcherVillager, R> {
    public ArcherVillagerRenderer(EntityRendererFactory.Context context) {
        super(context, new ArcherVillagerModel());
    }
}
