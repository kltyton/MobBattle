package com.kltyton.mob_battle.entity.customfireball.render;

import com.kltyton.mob_battle.entity.customfireball.CustomSuperBigFireballEntity;
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
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class CustomSuperBigFireballEntityRenderer<T extends CustomSuperBigFireballEntity> extends EntityRenderer<T, FlyingItemEntityRenderState> {
    public static final int growTime = 40;
    private final ItemModelManager itemModelManager;
    public final float scale;
    private final boolean lit;

    public CustomSuperBigFireballEntityRenderer(EntityRendererFactory.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemModelManager = ctx.getItemModelManager();
        this.scale = scale;
        this.lit = lit;
    }

    public CustomSuperBigFireballEntityRenderer(EntityRendererFactory.Context context) {
        this(context, 3.0F, true);
    }

    @Override
    protected int getBlockLight(T entity, BlockPos pos) {
        return this.lit ? 15 : super.getBlockLight(entity, pos);
    }

    public void render(FlyingItemEntityRenderState flyingItemEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        float progress = Math.min(flyingItemEntityRenderState.age / (float) growTime, 1.0f);
        float scaleMagnification = 1.0f + 2.0f * progress; // 1.0 -> 3.0
        matrixStack.scale(this.scale * scaleMagnification, this.scale * scaleMagnification, this.scale * scaleMagnification);
        matrixStack.multiply(this.dispatcher.getRotation());
        flyingItemEntityRenderState.itemRenderState.render(matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(flyingItemEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    public FlyingItemEntityRenderState createRenderState() {
        return new FlyingItemEntityRenderState();
    }

    public void updateRenderState(T entity, FlyingItemEntityRenderState flyingItemEntityRenderState, float f) {
        super.updateRenderState(entity, flyingItemEntityRenderState, f);
        this.itemModelManager.updateForNonLivingEntity(flyingItemEntityRenderState.itemRenderState, entity.getStack(), ItemDisplayContext.GROUND, entity);
    }
}
