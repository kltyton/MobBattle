package com.kltyton.mob_battle.entity.golem;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import net.minecraft.resources.Identifier;

public class StrongMinRenderer extends IronGolemRenderer {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            Mob_battle.MOD_ID,
            "textures/entity/golem/strong_min.png"
    );

    public StrongMinRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.layers.removeIf(layer -> layer instanceof IronGolemCrackinessLayer);
    }

    @Override
    public Identifier getTextureLocation(IronGolemRenderState state) {
        return TEXTURE;
    }
}
