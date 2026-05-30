package com.kltyton.mob_battle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

public final class SubmitRenderUtil {
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();

    private SubmitRenderUtil() {
    }

    public static void submitBillboardItem(ThrownItemRenderState state, PoseStack poseStack,
                                           SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                                           float scale) {
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(cameraState.orientation);
        state.item.submit(poseStack, renderTasks, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }

    public static void submitBlock(BlockModelResolver blockModelResolver, BlockModelRenderState blockRenderState,
                                   BlockState blockState, PoseStack poseStack, SubmitNodeCollector renderTasks,
                                   int light, int outlineColor) {
        blockModelResolver.update(blockRenderState, blockState, BLOCK_DISPLAY_CONTEXT);
        blockRenderState.submit(poseStack, renderTasks, light, OverlayTexture.NO_OVERLAY, outlineColor);
    }

    public static <S extends EntityRenderState> void submitModel(EntityModel<? super S> model, S state,
                                                                 PoseStack poseStack, SubmitNodeCollector renderTasks,
                                                                 Identifier texture) {
        renderTasks.submitModel(
                model,
                state,
                poseStack,
                texture,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor,
                null
        );
    }
}
