package com.kltyton.mob_battle.entity.lobster;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class LobsterEntityModel extends GeoModel<LobsterEntity> {
    private final Identifier model = Identifier.of(Mob_battle.MOD_ID, "lobster_entity");
    private final Identifier animations = Identifier.of(Mob_battle.MOD_ID, "lobster_entity");
    private final Identifier red_texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/lobster_entity/lobster_red.png");
    private final Identifier blue_texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/lobster_entity/lobster_blue.png");
    private final Identifier gray_texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/lobster_entity/lobster_gray.png");
    private final Identifier white_texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/lobster_entity/lobster_white.png");
    private final Identifier gold_texture = Identifier.of(Mob_battle.MOD_ID, "textures/entity/lobster_entity/lobster_gold.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
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
    public Identifier getAnimationResource(LobsterEntity animatable) {
        return animations;
    }
}
