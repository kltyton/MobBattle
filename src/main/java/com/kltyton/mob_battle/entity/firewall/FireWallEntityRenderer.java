package com.kltyton.mob_battle.entity.firewall;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class FireWallEntityRenderer extends EntityRenderer<FireWallEntity, EntityRenderState> {
    public FireWallEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public boolean shouldRender(FireWallEntity entity, net.minecraft.client.renderer.culling.Frustum frustum, double x, double y, double z) {
        // 默认 false，不渲染实体模型
        return false;
    }

    @Override
    public EntityRenderState createRenderState() {
        return null;
    }
}

