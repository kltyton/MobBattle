package com.kltyton.mob_battle.entity.drone.treatmentdrone;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class TreatmentDroneEntityModel extends GeoModel<TreatmentDroneEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "treatment_drone");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "treatment_drone");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/drone/treatment_drone.png");

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
