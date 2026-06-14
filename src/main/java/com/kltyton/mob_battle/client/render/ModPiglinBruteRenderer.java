package com.kltyton.mob_battle.client.render;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.resources.Identifier;

public class ModPiglinBruteRenderer extends PiglinRenderer {
    private static final Identifier PIGLIN_BRUTE_TEXTURE =
            Identifier.withDefaultNamespace("textures/entity/piglin/piglin_brute.png");

    public ModPiglinBruteRenderer(EntityRendererProvider.Context context) {
        super(
                context,
                ModelLayers.PIGLIN_BRUTE,
                ModelLayers.PIGLIN_BRUTE,
                ModelLayers.PIGLIN_BRUTE_ARMOR,
                ModelLayers.PIGLIN_BRUTE_ARMOR
        );
    }

    @Override
    public Identifier getTextureLocation(PiglinRenderState state) {
        return PIGLIN_BRUTE_TEXTURE;
    }
}
