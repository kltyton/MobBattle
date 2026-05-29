package com.kltyton.mob_battle.entity.meteorite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;

@Environment(EnvType.CLIENT)
public class MeteoriteEntityRender<T extends MeteoriteEntity> extends EntityRenderer<T, ThrownItemRenderState> {
    private final BlockRenderDispatcher blockRenderManager;

    public MeteoriteEntityRender(EntityRendererProvider.Context context) {
        super(context);
        // 从 context 中获取方块渲染管理器
        this.blockRenderManager = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(ThrownItemRenderState state, PoseStack matrices, MultiBufferSource vertices, int light) {
        matrices.pushPose();
        matrices.scale(3.0F, 3.0F, 3.0F);
        matrices.translate(-0.5F, -0.5F, -0.5F);
        float rotation = state.ageInTicks * 5.0F; // 根据年龄计算旋转角度
        matrices.mulPose(Axis.YP.rotationDegrees(rotation));
        matrices.mulPose(Axis.XP.rotationDegrees(rotation * 0.5F));
        // 3. 渲染方块
        // 使用岩浆块的默认状态
        this.blockRenderManager.renderSingleBlock(
                Blocks.MAGMA_BLOCK.defaultBlockState(),
                matrices,
                vertices,
                light,
                OverlayTexture.NO_OVERLAY
        );

        matrices.popPose();
        super.render(state, matrices, vertices, light);
    }

    @Override
    public ThrownItemRenderState createRenderState() {
        return new ThrownItemRenderState();
    }
}
