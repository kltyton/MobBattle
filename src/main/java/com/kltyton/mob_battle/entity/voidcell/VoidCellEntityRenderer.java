package com.kltyton.mob_battle.entity.voidcell;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VoidCellEntityRenderer <R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<VoidCellEntity, R> {
    public VoidCellEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new VoidCellEntityModel());
        this.shadowRadius = 0.1f;
    }

    @Override
    public void defaultRender(R renderState, MatrixStack poseStack, VertexConsumerProvider bufferSource, @Nullable RenderLayer renderType, @Nullable VertexConsumer buffer) {
        poseStack.push();
        // 缩放
        poseStack.scale(0.28f, 0.28f, 0.28f);
        super.defaultRender(renderState, poseStack, bufferSource, renderType, buffer);
        poseStack.pop();
    }

    @Nullable
    @Override
    public RenderLayer getRenderType(R renderState, Identifier texture) {
        return RenderLayer.getEntityTranslucentEmissive(texture);
    }
}