package com.kltyton.mob_battle.entity.sugarmanscorpion;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class SugarManScorpionModel extends GeoModel<SugarManScorpion> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "sugar_man_scorpion");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "sugar_man_scorpion");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/sugar_man_scorpion.png");
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
