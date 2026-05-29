package com.kltyton.mob_battle.entity.witherskeletonking.skill;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.WitherSkullRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.WitherSkull;

@Environment(EnvType.CLIENT)
public class WitherSkullBulletEntityRenderer extends EntityRenderer<WitherSkullBulletEntity, WitherSkullRenderState> {
    private static final ResourceLocation INVULNERABLE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_invulnerable.png");
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither.png");
    private final SkullModel model;

    public WitherSkullBulletEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SkullModel(context.bakeLayer(ModelLayers.WITHER_SKULL));
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild(PartNames.HEAD, CubeListBuilder.create().texOffs(0, 35).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(modelData, 64, 64);
    }

    protected int getBlockLight(WitherSkull witherSkullEntity, BlockPos blockPos) {
        return 15;
    }

    public void render(WitherSkullRenderState witherSkullEntityRenderState, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        matrixStack.pushPose();
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.renderType(this.getTextureLocation(witherSkullEntityRenderState)));
        this.model.setupAnim(0.0F, witherSkullEntityRenderState.yRot, witherSkullEntityRenderState.xRot);
        this.model.renderToBuffer(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
        super.render(witherSkullEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    private ResourceLocation getTextureLocation(WitherSkullRenderState state) {
        return state.isDangerous ? INVULNERABLE_TEXTURE : TEXTURE;
    }

    public WitherSkullRenderState createRenderState() {
        return new WitherSkullRenderState();
    }

    public void extractRenderState(WitherSkullBulletEntity witherSkullEntity, WitherSkullRenderState witherSkullEntityRenderState, float f) {
        super.extractRenderState(witherSkullEntity, witherSkullEntityRenderState, f);
        witherSkullEntityRenderState.isDangerous = false;
        witherSkullEntityRenderState.yRot = witherSkullEntity.getYRot(f);
        witherSkullEntityRenderState.xRot = witherSkullEntity.getXRot(f);
    }
}
