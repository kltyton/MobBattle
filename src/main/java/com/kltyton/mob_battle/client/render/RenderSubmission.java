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

/**
 * 客户端渲染节点提交入口。
 *
 * <p>统一物品公告板、方块模型与实体模型的 PoseStack 生命周期。</p>
 */
public final class RenderSubmission {
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();

    private RenderSubmission() {
    }

    /** 提交始终朝向摄像机的物品渲染节点。 */
    public static void submitBillboardItem(ThrownItemRenderState state, PoseStack poseStack,
                                           SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                                           float scale) {
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(cameraState.orientation);
        state.item.submit(poseStack, renderTasks, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }

    /** 更新方块模型状态并提交方块渲染节点。 */
    public static void submitBlock(BlockModelResolver blockModelResolver, BlockModelRenderState blockRenderState,
                                   BlockState blockState, PoseStack poseStack, SubmitNodeCollector renderTasks,
                                   int light, int outlineColor) {
        blockModelResolver.update(blockRenderState, blockState, BLOCK_DISPLAY_CONTEXT);
        blockRenderState.submit(poseStack, renderTasks, light, OverlayTexture.NO_OVERLAY, outlineColor);
    }

    /** 提交使用指定纹理的实体模型渲染节点。 */
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
