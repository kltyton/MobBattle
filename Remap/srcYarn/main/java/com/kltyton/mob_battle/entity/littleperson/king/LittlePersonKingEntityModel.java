package com.kltyton.mob_battle.entity.littleperson.king;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class LittlePersonKingEntityModel extends GeoModel<LittlePersonKingEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "little_person_king");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "little_person_king");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/little_person/little_person_king.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(LittlePersonKingEntity animatable) {
        return animations;
    }
}
