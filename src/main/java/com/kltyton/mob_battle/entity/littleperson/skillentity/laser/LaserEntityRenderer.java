package com.kltyton.mob_battle.entity.littleperson.skillentity.laser;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.ModModel;
import com.kltyton.mob_battle.client.render.SubmitRenderUtil;
import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class LaserEntityRenderer extends EntityRenderer<SkillProjectileEntity, TippableArrowRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/projectiles/laser.png");
    private final LaserEntityModel model;

    public LaserEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new LaserEntityModel(context.bakeLayer(ModModel.LASER));
    }

    @Override
    public void submit(TippableArrowRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        matrices.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        SubmitRenderUtil.submitModel(this.model, state, matrices, renderTasks, TEXTURE);
        matrices.popPose();
        super.submit(state, matrices, renderTasks, cameraState);
    }

    @Override
    public void extractRenderState(SkillProjectileEntity entity, TippableArrowRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.xRot = entity.getXRot(tickDelta);
        state.yRot = entity.getYRot(tickDelta);
    }

    @Override
    public TippableArrowRenderState createRenderState() {
        return new TippableArrowRenderState();
    }
}
