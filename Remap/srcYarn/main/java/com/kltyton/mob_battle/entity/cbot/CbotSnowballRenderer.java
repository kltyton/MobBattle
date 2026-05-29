package com.kltyton.mob_battle.entity.cbot;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.FlyingItemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;

@Environment(EnvType.CLIENT)
public class CbotSnowballRenderer extends EntityRenderer<CbotSnowballEntity, FlyingItemEntityRenderState> {
    private final ItemModelManager itemModelManager;

    public CbotSnowballRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelManager();
    }

    @Override
    public void render(FlyingItemEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.multiply(this.dispatcher.getRotation());
        state.itemRenderState.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        super.render(state, matrices, vertexConsumers, light);
    }

    @Override
    public FlyingItemEntityRenderState createRenderState() {
        return new FlyingItemEntityRenderState();
    }

    @Override
    public void updateRenderState(CbotSnowballEntity entity, FlyingItemEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        this.itemModelManager.updateForNonLivingEntity(state.itemRenderState, entity.getDisplayStack(), ItemDisplayContext.GROUND, entity);
    }
}
