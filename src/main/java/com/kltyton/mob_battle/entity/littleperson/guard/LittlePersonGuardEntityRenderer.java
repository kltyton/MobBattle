package com.kltyton.mob_battle.entity.littleperson.guard;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class LittlePersonGuardEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LittlePersonGuardEntity, R> {
    public LittlePersonGuardEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new LittlePersonGuardEntityModel());
    }
}

