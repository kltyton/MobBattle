package com.kltyton.mob_battle.entity.littleperson.guard;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class LittlePersonGuardEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LittlePersonGuardEntity, R> {
    public LittlePersonGuardEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new LittlePersonGuardEntityModel());
    }
}

