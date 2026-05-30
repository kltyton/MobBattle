package com.kltyton.mob_battle.entity.deepcreature;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class DeepCreatureEntityModel extends GeoModel<DeepCreatureEntity> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "deep_creature");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "deep_creature");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/deep_creature/deep_creature.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(DeepCreatureEntity animatable) {
        return animations;
    }
}
