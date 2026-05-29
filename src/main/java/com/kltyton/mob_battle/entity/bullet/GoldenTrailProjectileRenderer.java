package com.kltyton.mob_battle.entity.bullet;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.ModModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class GoldenTrailProjectileRenderer extends EntityRenderer<GoldenTrailProjectile, TippableArrowRenderState> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/projectiles/golden_trail_projectile.png");
    public static final ResourceLocation TIPPED_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/golden_trail_projectile.png");
    private final GoldenTrailProjectileModel model;

    public GoldenTrailProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new GoldenTrailProjectileModel(context.bakeLayer(ModModel.GOLDEN_TRAIL_PROJECTILE));
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

    @Override
    public void extractRenderState(GoldenTrailProjectile persistentProjectileEntity, TippableArrowRenderState projectileEntityRenderState, float f) {
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
