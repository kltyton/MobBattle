package com.kltyton.mob_battle.entity.vindicatorgeneral;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VindicatorGeneralEntityModel extends GeoModel<VindicatorGeneralEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "vindicator_general");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "vindicator_general");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/vindicator_general/vindicator_general.png");

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(VindicatorGeneralEntity animatable) {
        return animations;
    }
    @Override
    public void setCustomAnimations(AnimationState<VindicatorGeneralEntity> animationState) {
        super.setCustomAnimations(animationState);
        GeoBone head = getAnimationProcessor().getBone("Head");

        if (head != null) {
            float pitch = animationState.getDataOrDefault(DataTickets.ENTITY_PITCH, 0F);
            float yaw = animationState.getDataOrDefault(DataTickets.ENTITY_YAW, 0F);

            head.setRotX(pitch * Mth.DEG_TO_RAD);
            head.setRotY(-yaw * Mth.DEG_TO_RAD);
        }
    }
}