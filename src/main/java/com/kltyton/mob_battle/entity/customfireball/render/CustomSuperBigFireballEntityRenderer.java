package com.kltyton.mob_battle.entity.customfireball.render;

import com.kltyton.mob_battle.client.render.SubmitRenderUtil;
import com.kltyton.mob_battle.entity.customfireball.CustomSuperBigFireballEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;

@Environment(EnvType.CLIENT)
public class CustomSuperBigFireballEntityRenderer<T extends CustomSuperBigFireballEntity> extends EntityRenderer<T, ThrownItemRenderState> {
    public static final int growTime = 40;
    private final ItemModelResolver itemModelManager;
    public final float scale;
    private final boolean lit;

    public CustomSuperBigFireballEntityRenderer(EntityRendererProvider.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemModelManager = ctx.getItemModelResolver();
        this.scale = scale;
        this.lit = lit;
    }

    public CustomSuperBigFireballEntityRenderer(EntityRendererProvider.Context context) {
        this(context, 3.0F, true);
    }

    @Override
    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return this.lit ? 15 : super.getBlockLightLevel(entity, pos);
    }

    @Override
    public void submit(ThrownItemRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        float progress = Math.min(state.ageInTicks / (float) growTime, 1.0F);
        float scaleMagnification = 1.0F + 2.0F * progress;
        SubmitRenderUtil.submitBillboardItem(state, matrices, renderTasks, cameraState, this.scale * scaleMagnification);
        super.submit(state, matrices, renderTasks, cameraState);
    }

    public ThrownItemRenderState createRenderState() {
        return new ThrownItemRenderState();
    }

    public void extractRenderState(T entity, ThrownItemRenderState flyingItemEntityRenderState, float f) {
        super.extractRenderState(entity, flyingItemEntityRenderState, f);
        this.itemModelManager.updateForNonLiving(flyingItemEntityRenderState.item, entity.getItem(), ItemDisplayContext.GROUND, entity);
    }
}
