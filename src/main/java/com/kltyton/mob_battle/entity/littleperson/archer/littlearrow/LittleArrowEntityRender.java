package com.kltyton.mob_battle.entity.littleperson.archer.littlearrow;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.ModModel;
import com.kltyton.mob_battle.client.render.SubmitRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class LittleArrowEntityRender extends EntityRenderer<LittleArrowEntity, TippableArrowRenderState> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/projectiles/common_projectile.png");
    public static final Identifier TIPPED_TEXTURE = TEXTURE;
    private final LittleArrowEntityModel model;

    public LittleArrowEntityRender(EntityRendererProvider.Context context) {
        super(context);
        this.model = new LittleArrowEntityModel(context.bakeLayer(ModModel.LITTLE_ARROW));
    }

    @Override
    public void submit(TippableArrowRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        matrices.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        SubmitRenderUtil.submitModel(this.model, state, matrices, renderTasks, this.getTextureLocation(state));
        matrices.popPose();
        super.submit(state, matrices, renderTasks, cameraState);
    }

    public void extractRenderState(LittleArrowEntity persistentProjectileEntity, TippableArrowRenderState projectileEntityRenderState, float f) {
        super.extractRenderState(persistentProjectileEntity, projectileEntityRenderState, f);
        projectileEntityRenderState.xRot = persistentProjectileEntity.getXRot(f);
        projectileEntityRenderState.yRot = persistentProjectileEntity.getYRot(f);
        projectileEntityRenderState.shake = persistentProjectileEntity.shakeTime - f;
        projectileEntityRenderState.isTipped = persistentProjectileEntity.getColor() > 0;
    }

    protected Identifier getTextureLocation(TippableArrowRenderState arrowEntityRenderState) {
        return arrowEntityRenderState.isTipped ? TIPPED_TEXTURE : TEXTURE;
    }

    public TippableArrowRenderState createRenderState() {
        return new TippableArrowRenderState();
    }
}
