package com.kltyton.mob_battle.entity.bullet;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;

@Environment(EnvType.CLIENT)
public class GoldenBulletEntityRenderer extends EntityRenderer<GoldenBulletEntity, ThrownItemRenderState> {
    private final ItemModelResolver itemModelManager;

    public GoldenBulletEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelResolver();
    }

    @Override
    public void render(ThrownItemRenderState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();
        matrices.scale(0.75F, 0.75F, 0.75F);
        matrices.mulPose(this.entityRenderDispatcher.cameraOrientation());
        state.item.render(matrices, vertexConsumers, light, OverlayTexture.NO_OVERLAY);
        matrices.popPose();
        super.render(state, matrices, vertexConsumers, light);
    }

    @Override
    public ThrownItemRenderState createRenderState() {
        return new ThrownItemRenderState();
    }

    @Override
    public void extractRenderState(GoldenBulletEntity entity, ThrownItemRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        this.itemModelManager.updateForNonLiving(state.item, entity.getDisplayStack(), ItemDisplayContext.GROUND, entity);
    }
}
