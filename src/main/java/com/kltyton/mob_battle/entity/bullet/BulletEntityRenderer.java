package com.kltyton.mob_battle.entity.bullet;

import com.kltyton.mob_battle.client.render.SubmitRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.Blocks;

public class BulletEntityRenderer extends EntityRenderer<BulletEntity, ArrowRenderState> {
    private final BlockModelResolver blockModelResolver;
    private final BlockModelRenderState blockRenderState = new BlockModelRenderState();

    public BulletEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockModelResolver = context.getBlockModelResolver();
    }

    @Override
    public void submit(ArrowRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        matrices.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        matrices.scale(0.1F, 0.1F, 0.1F);
        SubmitRenderUtil.submitBlock(this.blockModelResolver, this.blockRenderState, Blocks.IRON_BLOCK.defaultBlockState(),
                matrices, renderTasks, state.lightCoords, state.outlineColor);
        matrices.popPose();
        super.submit(state, matrices, renderTasks, cameraState);
    }

    @Override
    public void extractRenderState(BulletEntity entity, ArrowRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.xRot = entity.getXRot(tickDelta);
        state.yRot = entity.getYRot(tickDelta);
        state.shake = entity.shakeTime - tickDelta;
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }

}
