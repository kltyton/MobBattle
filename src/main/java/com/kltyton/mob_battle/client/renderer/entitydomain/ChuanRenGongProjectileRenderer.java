package com.kltyton.mob_battle.client.renderer.entitydomain;

import com.kltyton.mob_battle.client.render.RenderSubmission;
import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

/** 传仁工子弹渲染器：先按弹体朝向旋转，再提交模型，避免模型位置偏移。 */
@Environment(EnvType.CLIENT)
public final class ChuanRenGongProjectileRenderer<T extends SkillProjectileEntity>
        extends EntityRenderer<T, TippableArrowRenderState> {
    private final EntityModel<TippableArrowRenderState> model;
    private final Identifier texture;

    public ChuanRenGongProjectileRenderer(EntityRendererProvider.Context context,
                                           ModelLayerLocation layer,
                                           Identifier texture) {
        super(context);
        this.model = new ChuanRenGongProjectileModel(context.bakeLayer(layer));
        this.texture = texture;
    }

    @Override
    public void submit(TippableArrowRenderState state, PoseStack matrices,
                       SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        matrices.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        RenderSubmission.submitModel(this.model, state, matrices, renderTasks, this.texture);
        matrices.popPose();
        super.submit(state, matrices, renderTasks, cameraState);
    }

    @Override
    public void extractRenderState(T entity, TippableArrowRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.xRot = entity.getXRot(tickDelta);
        state.yRot = entity.getYRot(tickDelta);
    }

    @Override
    public TippableArrowRenderState createRenderState() {
        return new TippableArrowRenderState();
    }
}
