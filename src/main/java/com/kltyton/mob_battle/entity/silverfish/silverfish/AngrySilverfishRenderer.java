package com.kltyton.mob_battle.entity.silverfish.silverfish;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SilverfishRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class AngrySilverfishRenderer extends SilverfishRenderer {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            Mob_battle.MOD_ID,
            "textures/entity/silverfish/angry_silverfish.png"
    );

    public AngrySilverfishRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
