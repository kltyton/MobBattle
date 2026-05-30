package com.kltyton.mob_battle.entity.meteorite;

import com.kltyton.mob_battle.client.render.SubmitRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.Blocks;

@Environment(EnvType.CLIENT)
public class MeteoriteEntityRender<T extends MeteoriteEntity> extends EntityRenderer<T, ThrownItemRenderState> {
    private final BlockModelResolver blockModelResolver;
    private final BlockModelRenderState blockRenderState = new BlockModelRenderState();

    public MeteoriteEntityRender(EntityRendererProvider.Context context) {
        super(context);
        this.blockModelResolver = context.getBlockModelResolver();
    }

    @Override
    public void submit(ThrownItemRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.scale(3.0F, 3.0F, 3.0F);
        matrices.translate(-0.5F, -0.5F, -0.5F);
        float rotation = state.ageInTicks * 5.0F;
        matrices.mulPose(Axis.YP.rotationDegrees(rotation));
        matrices.mulPose(Axis.XP.rotationDegrees(rotation * 0.5F));
        SubmitRenderUtil.submitBlock(this.blockModelResolver, this.blockRenderState, Blocks.MAGMA_BLOCK.defaultBlockState(),
                matrices, renderTasks, state.lightCoords, state.outlineColor);
        matrices.popPose();
        super.submit(state, matrices, renderTasks, cameraState);
    }

    @Override
    public ThrownItemRenderState createRenderState() {
        return new ThrownItemRenderState();
    }
}
