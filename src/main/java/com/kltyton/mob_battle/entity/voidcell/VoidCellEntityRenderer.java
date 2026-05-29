package com.kltyton.mob_battle.entity.voidcell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VoidCellEntityRenderer <R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<VoidCellEntity, R> {
    public VoidCellEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new VoidCellEntityModel());
        this.shadowRadius = 0.1f;
    }

    @Override
    public void defaultRender(R renderState, PoseStack poseStack, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer) {
        poseStack.pushPose();
        // 缩放
        poseStack.scale(0.28f, 0.28f, 0.28f);
        super.defaultRender(renderState, poseStack, bufferSource, renderType, buffer);
        poseStack.popPose();
    }

    @Nullable
    @Override
    public RenderType getRenderType(R renderState, ResourceLocation texture) {
        return RenderType.entityTranslucentEmissive(texture);
    }
}