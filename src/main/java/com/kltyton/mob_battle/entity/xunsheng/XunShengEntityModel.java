package com.kltyton.mob_battle.entity.xunsheng;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class XunShengEntityModel extends GeoModel<XunShengEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "xunsheng");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "xunsheng");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/xunsheng.png");

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(XunShengEntity animatable) {
        return this.animations;
    }
    @Override
    public void setCustomAnimations(AnimationState<XunShengEntity> animationState) {
        super.setCustomAnimations(animationState);
        GeoBone head = getAnimationProcessor().getBone("1");

        if (head != null) {
            float pitch = animationState.getData(DataTickets.ENTITY_PITCH);
            float yaw = animationState.getData(DataTickets.ENTITY_YAW);

            head.setRotX(-pitch * Mth.DEG_TO_RAD);
            head.setRotY(-yaw * Mth.DEG_TO_RAD);
        }
    }
}
