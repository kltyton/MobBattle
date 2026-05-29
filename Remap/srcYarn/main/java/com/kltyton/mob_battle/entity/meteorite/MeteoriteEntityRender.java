package com.kltyton.mob_battle.entity.meteorite;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.FlyingItemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class MeteoriteEntityRender<T extends MeteoriteEntity> extends EntityRenderer<T, FlyingItemEntityRenderState> {
    private final BlockRenderManager blockRenderManager;

    public MeteoriteEntityRender(EntityRendererFactory.Context context) {
        super(context);
        // 从 context 中获取方块渲染管理器
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(FlyingItemEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        matrices.push();
        matrices.scale(3.0F, 3.0F, 3.0F);
        matrices.translate(-0.5F, -0.5F, -0.5F);
        float rotation = state.age * 5.0F; // 根据年龄计算旋转角度
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation * 0.5F));
        // 3. 渲染方块
        // 使用岩浆块的默认状态
        this.blockRenderManager.renderBlockAsEntity(
                Blocks.MAGMA_BLOCK.getDefaultState(),
                matrices,
                vertices,
                light,
                OverlayTexture.DEFAULT_UV
        );

        matrices.pop();
        super.render(state, matrices, vertices, light);
    }

    @Override
    public FlyingItemEntityRenderState createRenderState() {
        return new FlyingItemEntityRenderState();
    }
}
