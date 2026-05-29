package com.kltyton.mob_battle.entity.littleperson.skillentity.spearbullet;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.ModModel;
import com.kltyton.mob_battle.entity.littleperson.archer.littlearrow.LittleArrowEntity;
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

public class SpearBulletEntityRenderer extends EntityRenderer<LittleArrowEntity, TippableArrowRenderState> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/projectiles/spear_bullet.png");
    public static final ResourceLocation TIPPED_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/spear_bullet.png");
    private final SpearBulletEntityModel model;

    public SpearBulletEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SpearBulletEntityModel(context.bakeLayer(ModModel.SPEAR_BULLET));
    }
    @Override
    public void render(TippableArrowRenderState projectileEntityRenderState, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        matrixStack.pushPose();
        matrixStack.mulPose(Axis.YP.rotationDegrees(projectileEntityRenderState.yRot - 90.0F));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(projectileEntityRenderState.xRot));
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.entityCutout(this.getTextureLocation(projectileEntityRenderState)));
        this.model.setupAnim(projectileEntityRenderState);
        this.model.renderToBuffer(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
        super.render(projectileEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    public void extractRenderState(LittleArrowEntity persistentProjectileEntity, TippableArrowRenderState projectileEntityRenderState, float f) {
        super.extractRenderState(persistentProjectileEntity, projectileEntityRenderState, f);
        projectileEntityRenderState.xRot = persistentProjectileEntity.getXRot(f);
        projectileEntityRenderState.yRot = persistentProjectileEntity.getYRot(f);
        projectileEntityRenderState.shake = persistentProjectileEntity.shakeTime - f;
        projectileEntityRenderState.isTipped = persistentProjectileEntity.getColor() > 0;
    }

    protected ResourceLocation getTextureLocation(TippableArrowRenderState arrowEntityRenderState) {
        return arrowEntityRenderState.isTipped ? TIPPED_TEXTURE : TEXTURE;
    }

    public TippableArrowRenderState createRenderState() {
        return new TippableArrowRenderState();
    }
}
