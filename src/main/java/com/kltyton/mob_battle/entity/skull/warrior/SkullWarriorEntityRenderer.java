package com.kltyton.mob_battle.entity.skull.warrior;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class SkullWarriorEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<SkullWarriorEntity, R> {
    public SkullWarriorEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new SkullWarriorEntityModel());
    }
}
