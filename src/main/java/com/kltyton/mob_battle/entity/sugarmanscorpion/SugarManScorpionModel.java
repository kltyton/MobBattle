package com.kltyton.mob_battle.entity.sugarmanscorpion;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class SugarManScorpionModel extends GeoModel<SugarManScorpion> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "sugar_man_scorpion");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "sugar_man_scorpion");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/sugar_man_scorpion.png");
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return this.model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return this.texture;
    }

    @Override
    public Identifier getAnimationResource(SugarManScorpion animatable) {
        return this.animations;
    }
}
