package com.kltyton.mob_battle.entity.deepcreature;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DeepCreatureEntityModel extends GeoModel<DeepCreatureEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "deep_creature");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "deep_creature");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/deep_creature/deep_creature.png");

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
