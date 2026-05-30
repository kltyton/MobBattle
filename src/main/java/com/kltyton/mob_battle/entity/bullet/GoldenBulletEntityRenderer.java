package com.kltyton.mob_battle.entity.bullet;

import com.kltyton.mob_battle.client.render.SubmitRenderUtil;
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
import net.minecraft.world.item.ItemDisplayContext;

@Environment(EnvType.CLIENT)
public class GoldenBulletEntityRenderer extends EntityRenderer<GoldenBulletEntity, ThrownItemRenderState> {
    private final ItemModelResolver itemModelManager;

    public GoldenBulletEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelResolver();
    }

    @Override
    public void submit(ThrownItemRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        SubmitRenderUtil.submitBillboardItem(state, matrices, renderTasks, cameraState, 0.75F);
        super.submit(state, matrices, renderTasks, cameraState);
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
