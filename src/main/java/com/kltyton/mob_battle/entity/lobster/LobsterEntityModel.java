package com.kltyton.mob_battle.entity.lobster;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class LobsterEntityModel extends GeoModel<LobsterEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "lobster_entity");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "lobster_entity");
    private final ResourceLocation red_texture = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/lobster_entity/lobster_red.png");
    private final ResourceLocation blue_texture = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/lobster_entity/lobster_blue.png");
    private final ResourceLocation gray_texture = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/lobster_entity/lobster_gray.png");
    private final ResourceLocation white_texture = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/lobster_entity/lobster_white.png");
    private final ResourceLocation gold_texture = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/lobster_entity/lobster_gold.png");

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        LobsterVariant variant = renderState.getGeckolibData(LobsterEntityRenderer.LOBSTER_VARIANT);

        if (variant == null) {
            return red_texture;
        }
        return switch (variant) {
            case RED -> red_texture;
            case BLUE -> blue_texture;
            case GRAY -> gray_texture;
            case WHITE -> white_texture;
            case GOLD -> gold_texture;
        };
    }

    @Override
    public ResourceLocation getAnimationResource(LobsterEntity animatable) {
        return animations;
    }
}
