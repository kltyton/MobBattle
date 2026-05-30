package com.kltyton.mob_battle.entity.drone.treatmentdrone;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class TreatmentDroneEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<TreatmentDroneEntity, R> {
    public TreatmentDroneEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new TreatmentDroneEntityModel());
    }
}
