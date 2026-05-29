package com.kltyton.mob_battle.entity.drone.attackdrone;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class AttackDroneEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<AttackDroneEntity, R> {
    public AttackDroneEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new AttackDroneEntityModel());
    }
}
