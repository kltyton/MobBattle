package com.kltyton.mob_battle.entity.bullet;

import net.minecraft.block.Blocks;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class BulletEntityRenderer extends EntityRenderer<BulletEntity, ProjectileEntityRenderState> {

    private final BlockRenderManager blockRenderManager;

    public BulletEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(ProjectileEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw - 90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.pitch));

        matrices.scale(0.1f, 0.1f, 0.1f);

        blockRenderManager.renderBlockAsEntity(
                Blocks.IRON_BLOCK.getDefaultState(),
                matrices,
                vertexConsumers,
                light,
                OverlayTexture.DEFAULT_UV
        );
        matrices.pop();
        super.render(state, matrices, vertexConsumers, light);
    }

    @Override
    public void updateRenderState(BulletEntity entity, ProjectileEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.pitch = entity.getLerpedPitch(tickDelta);
        state.yaw = entity.getLerpedYaw(tickDelta);
        state.shake = entity.shake - tickDelta;
    }

    @Override
    public ProjectileEntityRenderState createRenderState() {
        return new ProjectileEntityRenderState();
    }

}
