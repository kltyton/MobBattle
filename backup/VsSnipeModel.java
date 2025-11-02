package com.kltyton.mob_battle.items.tool.snipe;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VsSnipeModel extends GeoModel<VsSnipe> {
    public VsSnipeModel() {
        super();
    }
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "vs_snipe");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "vs_snipe");
    private final Identifier texture = Identifier.of(Mob_battle.MOD_ID, "textures/item/vs_snipe.png");
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(VsSnipe animatable) {
        return animations;
    }
}
