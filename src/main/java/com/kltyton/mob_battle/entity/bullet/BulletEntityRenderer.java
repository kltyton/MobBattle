package com.kltyton.mob_battle.entity.bullet;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;

public class BulletEntityRenderer extends EntityRenderer<BulletEntity, ArrowRenderState> {

    private final BlockRenderDispatcher blockRenderManager;

    public BulletEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderManager = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(ArrowRenderState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        matrices.mulPose(Axis.ZP.rotationDegrees(state.xRot));

        matrices.scale(0.1f, 0.1f, 0.1f);

        blockRenderManager.renderSingleBlock(
                Blocks.IRON_BLOCK.defaultBlockState(),
                matrices,
                vertexConsumers,
                light,
                OverlayTexture.NO_OVERLAY
        );
        matrices.popPose();
        super.render(state, matrices, vertexConsumers, light);
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
