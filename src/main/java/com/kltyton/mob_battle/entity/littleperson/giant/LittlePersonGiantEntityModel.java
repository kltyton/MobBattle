package com.kltyton.mob_battle.entity.littleperson.giant;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class LittlePersonGiantEntityModel extends GeoModel<LittlePersonGiantEntity> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "little_person_giant");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "little_person_giant");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/little_person/little_person_giant.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(LittlePersonGiantEntity animatable) {
        return animations;
    }
}
