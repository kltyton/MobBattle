package com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.ModModel;
import com.kltyton.mob_battle.client.render.SubmitRenderUtil;
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

public class MissileEntityRenderer extends EntityRenderer<MissileEntity, TippableArrowRenderState> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/projectiles/missile.png");
    public static final Identifier TIPPED_TEXTURE = Identifier.withDefaultNamespace("textures/entity/projectiles/missile.png");
    private final MissileEntityModel model;

    public MissileEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new MissileEntityModel(context.bakeLayer(ModModel.MISSILE));
    }

    @Override
    public void submit(TippableArrowRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(state.yRot));
        matrices.mulPose(Axis.XP.rotationDegrees(state.xRot));
        SubmitRenderUtil.submitModel(this.model, state, matrices, renderTasks, this.getTextureLocation(state));
        matrices.popPose();
        super.submit(state, matrices, renderTasks, cameraState);
    }

    public void extractRenderState(MissileEntity persistentProjectileEntity, TippableArrowRenderState projectileEntityRenderState, float f) {
        super.extractRenderState(persistentProjectileEntity, projectileEntityRenderState, f);
        projectileEntityRenderState.xRot = persistentProjectileEntity.getXRot(f);
        projectileEntityRenderState.yRot = persistentProjectileEntity.getYRot(f);
    }

    protected Identifier getTextureLocation(TippableArrowRenderState arrowEntityRenderState) {
        return arrowEntityRenderState.isTipped ? TIPPED_TEXTURE : TEXTURE;
    }

    public TippableArrowRenderState createRenderState() {
        return new TippableArrowRenderState();
    }
}
