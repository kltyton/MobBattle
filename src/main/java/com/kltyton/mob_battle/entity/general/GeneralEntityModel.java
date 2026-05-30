package com.kltyton.mob_battle.entity.general;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class GeneralEntityModel<T extends GeoAnimatable> extends GeoModel<T> {
    public enum RenderTypes {
        TRANSLUCENT,
        CUTOUT
    }
    public String name;
    public boolean hasHand;
    public RenderTypes renderLayer;
    public GeneralEntityModel(String name, boolean hasHand, RenderTypes renderLayer) {
        super();
        this.name = name;
        this.hasHand = hasHand;
        this.renderLayer = renderLayer;
    }
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, name);
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/" + name + "/" + name + ".png");
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, name);
    }
    public RenderType getRenderType(GeoRenderState renderState, Identifier texture) {
        if (renderLayer != null) {
            switch (renderLayer) {
                case TRANSLUCENT -> {
                    return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucentEmissive(texture);
                }
                case CUTOUT -> {
                    return net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(texture);
                }
            }
        }
        return net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(texture);
    }
}
