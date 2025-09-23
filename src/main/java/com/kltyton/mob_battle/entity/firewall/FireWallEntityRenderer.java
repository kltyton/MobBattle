package com.kltyton.mob_battle.entity.firewall;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;

public class FireWallEntityRenderer extends EntityRenderer<FireWallEntity, EntityRenderState> {
    public FireWallEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public boolean shouldRender(FireWallEntity entity, net.minecraft.client.render.Frustum frustum, double x, double y, double z) {
        // 默认 false，不渲染实体模型
        return false;
    }

    @Override
    public EntityRenderState createRenderState() {
        return null;
    }
}

