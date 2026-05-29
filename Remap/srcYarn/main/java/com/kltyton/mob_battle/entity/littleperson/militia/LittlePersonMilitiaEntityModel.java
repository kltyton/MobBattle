package com.kltyton.mob_battle.entity.littleperson.militia;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class LittlePersonMilitiaEntityModel extends GeoModel<LittlePersonMilitiaEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "little_person_militia");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "little_person_militia");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/little_person/little_person_militia.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(LittlePersonMilitiaEntity animatable) {
        return animations;
    }
}
