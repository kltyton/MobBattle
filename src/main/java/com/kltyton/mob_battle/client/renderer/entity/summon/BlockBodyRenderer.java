package com.kltyton.mob_battle.client.renderer.entity.summon;

import com.kltyton.mob_battle.entity.summon.presence.BlockAppearance;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;

/** 按实体快照提交原版方块模型；箱子等动态方块使用其原生物品特殊渲染器。 */
public final class BlockBodyRenderer<T extends Entity & BlockAppearance> extends EntityRenderer<T, BlockBodyRenderer.State> {
    private static final BlockDisplayContext DISPLAY = BlockDisplayContext.create();
    private final BlockModelResolver blocks;
    private final ItemModelResolver items;

    public static final class State extends EntityRenderState {
        final BlockModelRenderState block = new BlockModelRenderState();
        final ItemStackRenderState item = new ItemStackRenderState();
        boolean special;
        float scale;
        float roll;
        float yaw;
    }

    public BlockBodyRenderer(EntityRendererProvider.Context context) {
        super(context);
        blocks = context.getBlockModelResolver();
        items = context.getItemModelResolver();
        shadowRadius = 0.3F;
    }

    @Override public State createRenderState() { return new State(); }

    @Override
    public void extractRenderState(T entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        var block = entity.visualBlockState();
        state.special = block.getRenderShape() != RenderShape.MODEL;
        if (state.special) items.updateForNonLiving(state.item, new ItemStack(block.getBlock()), ItemDisplayContext.NONE, entity);
        else blocks.update(state.block, block, DISPLAY);
        state.scale = entity.visualScale();
        state.roll = entity.visualRoll(partialTick);
        state.yaw = entity.getYRot(partialTick);
    }

    @Override
    public void submit(State state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        poses.pushPose();
        poses.translate(0, state.scale * 0.5, 0);
        poses.mulPose(Axis.YP.rotationDegrees(-state.yaw));
        poses.mulPose(Axis.XP.rotationDegrees(state.roll));
        poses.scale(state.scale, state.scale, state.scale);
        if (state.special) state.item.submit(poses, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        else {
            poses.translate(-0.5, -0.5, -0.5);
            state.block.submit(poses, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        }
        poses.popPose();
        super.submit(state, poses, collector, camera);
    }
}
