package com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.ModModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class MissileEntityRenderer extends EntityRenderer<MissileEntity, TippableArrowRenderState> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/projectiles/missile.png");
    public static final ResourceLocation TIPPED_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/missile.png");
    private final MissileEntityModel model;

    public MissileEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new MissileEntityModel(context.bakeLayer(ModModel.MISSILE));
    }
    @Override
    public void render(TippableArrowRenderState state, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        matrixStack.pushPose();
        matrixStack.mulPose(Axis.YP.rotationDegrees(state.yRot));
        matrixStack.mulPose(Axis.XP.rotationDegrees(state.xRot));
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.entityCutout(this.getTextureLocation(state)));
        // 纭繚浣犵殑 setAngles 鍐呴儴娌℃湁浼氱疮鍔犳棆杞殑鎿嶄綔
        this.model.setupAnim(state);
        this.model.renderToBuffer(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);

        matrixStack.popPose();
        super.render(state, matrixStack, vertexConsumerProvider, i);
    }

    public void extractRenderState(MissileEntity persistentProjectileEntity, TippableArrowRenderState projectileEntityRenderState, float f) {
        super.extractRenderState(persistentProjectileEntity, projectileEntityRenderState, f);
        projectileEntityRenderState.xRot = persistentProjectileEntity.getXRot(f);
        projectileEntityRenderState.yRot = persistentProjectileEntity.getYRot(f);
    }

    protected ResourceLocation getTextureLocation(TippableArrowRenderState arrowEntityRenderState) {
        return arrowEntityRenderState.isTipped ? TIPPED_TEXTURE : TEXTURE;
    }

    public TippableArrowRenderState createRenderState() {
        return new TippableArrowRenderState();
    }
}