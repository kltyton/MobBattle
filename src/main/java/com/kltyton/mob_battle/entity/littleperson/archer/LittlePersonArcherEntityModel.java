package com.kltyton.mob_battle.entity.littleperson.archer;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class LittlePersonArcherEntityModel extends GeoModel<LittlePersonArcherEntity> {
    private final Identifier model = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "little_person_archer");
    private final Identifier animations = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "little_person_archer");
    private final Identifier texture = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/little_person/little_person_archer.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(LittlePersonArcherEntity animatable) {
        return animations;
    }
}
