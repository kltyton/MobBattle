package com.kltyton.mob_battle.entity.drone.treatmentdrone;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class TreatmentDroneEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<TreatmentDroneEntity, R> {
    public TreatmentDroneEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new TreatmentDroneEntityModel());
    }
}
