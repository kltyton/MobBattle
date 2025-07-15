package com.kltyton.mob_battle.entity.xunsheng;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class XunShengEntityModel extends GeoModel<XunShengEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "xunsheng");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "xunsheng");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/xunsheng.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return this.model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return this.texture;
    }

    @Override
    public Identifier getAnimationResource(XunShengEntity animatable) {
        return this.animations;
    }
}
