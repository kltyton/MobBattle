package com.kltyton.mob_battle.entity.skull.warrior;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class SkullWarriorEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<SkullWarriorEntity, R> {
    public SkullWarriorEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SkullWarriorEntityModel());
    }
}
