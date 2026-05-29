package com.kltyton.mob_battle.entity.irongolem;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VillagerIronGolemEntityModel extends GeoModel<VillagerIronGolemEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "irongolem");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "irongolem");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/irongolem/irongolem.png");

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(VillagerIronGolemEntity animatable) {
        return animations;
    }
    @Override
    public void setCustomAnimations(AnimationState<VillagerIronGolemEntity> animationState) {
        super.setCustomAnimations(animationState);
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            float pitch = animationState.getDataOrDefault(DataTickets.ENTITY_PITCH, 0F);
            float yaw = animationState.getDataOrDefault(DataTickets.ENTITY_YAW, 0F);

            head.setRotX(-pitch * Mth.DEG_TO_RAD);
            head.setRotY(-yaw * Mth.DEG_TO_RAD);
        }
    }
}