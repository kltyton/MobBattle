package com.kltyton.mob_battle.entity.drone.attackdrone;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class AttackDroneEntityModel extends GeoModel<AttackDroneEntity> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "attack_drone");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "attack_drone");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/drone/attack_drone.png");

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
