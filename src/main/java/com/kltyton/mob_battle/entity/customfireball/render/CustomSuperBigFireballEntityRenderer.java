package com.kltyton.mob_battle.entity.customfireball.render;

import com.kltyton.mob_battle.entity.customfireball.CustomSuperBigFireballEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
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

    public void render(ThrownItemRenderState flyingItemEntityRenderState, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        matrixStack.pushPose();
        float progress = Math.min(flyingItemEntityRenderState.ageInTicks / (float) growTime, 1.0f);
        float scaleMagnification = 1.0f + 2.0f * progress; // 1.0 -> 3.0
        matrixStack.scale(this.scale * scaleMagnification, this.scale * scaleMagnification, this.scale * scaleMagnification);
        matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        flyingItemEntityRenderState.item.render(matrixStack, vertexConsumerProvider, i, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
        super.render(flyingItemEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    public ThrownItemRenderState createRenderState() {
        return new ThrownItemRenderState();
    }

    public void extractRenderState(T entity, ThrownItemRenderState flyingItemEntityRenderState, float f) {
        super.extractRenderState(entity, flyingItemEntityRenderState, f);
        this.itemModelManager.updateForNonLiving(flyingItemEntityRenderState.item, entity.getItem(), ItemDisplayContext.GROUND, entity);
    }
}
