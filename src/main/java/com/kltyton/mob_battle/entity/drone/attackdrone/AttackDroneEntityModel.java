package com.kltyton.mob_battle.entity.drone.attackdrone;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class AttackDroneEntityModel extends GeoModel<AttackDroneEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "attack_drone");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "attack_drone");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/drone/attack_drone.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(AttackDroneEntity animatable) {
        return animations;
    }
}
