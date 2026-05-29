package com.kltyton.mob_battle.entity.drone.attackdrone;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class AttackDroneEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<AttackDroneEntity, R> {
    public AttackDroneEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new AttackDroneEntityModel());
    }
}
