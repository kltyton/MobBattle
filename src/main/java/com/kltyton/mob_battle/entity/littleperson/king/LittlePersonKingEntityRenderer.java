package com.kltyton.mob_battle.entity.littleperson.king;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class LittlePersonKingEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LittlePersonKingEntity, R> {
    public LittlePersonKingEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new LittlePersonKingEntityModel());
    }
}

