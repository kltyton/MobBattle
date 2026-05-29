package com.kltyton.mob_battle.entity.general;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

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
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, name);
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/" + name + "/" + name + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, name);
    }
    @Override
    public void setCustomAnimations(AnimationState<T> animationState) {
        super.setCustomAnimations(animationState);
        if (hasHand) {
            GeoBone head = getAnimationProcessor().getBone("Head");

            if (head != null) {
                float pitch = animationState.getDataOrDefault(DataTickets.ENTITY_PITCH, 0F);
                float yaw = animationState.getDataOrDefault(DataTickets.ENTITY_YAW, 0F);

                head.setRotX(-pitch * Mth.DEG_TO_RAD);
                head.setRotY(-yaw * Mth.DEG_TO_RAD);
            }
        }
    }
    public RenderType getRenderType(GeoRenderState renderState, ResourceLocation texture) {
        if (renderLayer != null) {
            switch (renderLayer) {
                case TRANSLUCENT -> {
                    return RenderType.entityTranslucentEmissive(texture);
                }
                case CUTOUT -> {
                    return RenderType.entityCutoutNoCull(texture);
                }
            }
        }
        return RenderType.entityCutoutNoCull(texture);
    }
}
