package com.kltyton.mob_battle.entity.witherskeletonking.skill;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.WitherSkullRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;

@Environment(EnvType.CLIENT)
public class WitherSkullBulletEntityRenderer extends EntityRenderer<WitherSkullBulletEntity, WitherSkullRenderState> {
    private static final Identifier INVULNERABLE_TEXTURE = Identifier.withDefaultNamespace("textures/entity/wither/wither_invulnerable.png");
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/wither/wither.png");
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

    @Override
    protected int getBlockLightLevel(WitherSkullBulletEntity witherSkullEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void submit(WitherSkullRenderState state, PoseStack matrices, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.scale(-1.0F, -1.0F, 1.0F);
        renderTasks.submitModel(
                this.model,
                state.modelState,
                matrices,
                this.getTextureLocation(state),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor,
                null
        );
        matrices.popPose();
        super.submit(state, matrices, renderTasks, cameraState);
    }

    private Identifier getTextureLocation(WitherSkullRenderState state) {
        return state.isDangerous ? INVULNERABLE_TEXTURE : TEXTURE;
    }

    public WitherSkullRenderState createRenderState() {
        return new WitherSkullRenderState();
    }

    public void extractRenderState(WitherSkullBulletEntity witherSkullEntity, WitherSkullRenderState witherSkullEntityRenderState, float f) {
        super.extractRenderState(witherSkullEntity, witherSkullEntityRenderState, f);
        witherSkullEntityRenderState.isDangerous = false;
        witherSkullEntityRenderState.modelState.animationPos = 0.0F;
        witherSkullEntityRenderState.modelState.yRot = witherSkullEntity.getYRot(f);
        witherSkullEntityRenderState.modelState.xRot = witherSkullEntity.getXRot(f);
    }
}
