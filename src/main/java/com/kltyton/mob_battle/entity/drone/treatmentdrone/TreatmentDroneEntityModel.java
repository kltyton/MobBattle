package com.kltyton.mob_battle.entity.drone.treatmentdrone;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class TreatmentDroneEntityModel extends GeoModel<TreatmentDroneEntity> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "treatment_drone");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "treatment_drone");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/drone/treatment_drone.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(TreatmentDroneEntity animatable) {
        return animations;
    }
}
