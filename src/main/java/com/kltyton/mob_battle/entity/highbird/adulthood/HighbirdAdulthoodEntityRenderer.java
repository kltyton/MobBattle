package com.kltyton.mob_battle.entity.highbird.adulthood;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.util.RenderUtil;

public class HighbirdAdulthoodEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<HighbirdAdulthoodEntity, R> {
    public HighbirdAdulthoodEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new HighbirdAdulthoodEntityModel());
    }

    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 90f;
    }

    @Override
    public int getPackedOverlay(HighbirdAdulthoodEntity animatable, Void relatedObject, float u, float partialTick) {
        if (!(animatable instanceof LivingEntity entity))
            return OverlayTexture.NO_OVERLAY;
        if (entity.isDeadOrDying()) return OverlayTexture.NO_OVERLAY;
        return OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(entity.hurtTime > 0));
    }

    @Override
    protected void applyRotations(R renderState, PoseStack poseStack, float nativeScale) {
        float rotationYaw = renderState.getGeckolibData(DataTickets.ENTITY_BODY_YAW);

        if (renderState.getGeckolibData(DataTickets.IS_SHAKING))
            rotationYaw += (float) (Math.cos(renderState.ageInTicks * 3.25d) * Math.PI * 0.4d);

        boolean sleeping = renderState.getGeckolibData(DataTickets.ENTITY_POSE) == Pose.SLEEPING;

        if (!sleeping)
            poseStack.mulPose(Axis.YP.rotationDegrees(180f - rotationYaw));

        if (renderState instanceof LivingEntityRenderState livingRenderState) {
            if (livingRenderState.isAutoSpinAttack) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90f - livingRenderState.xRot));
                poseStack.mulPose(Axis.YP.rotationDegrees(renderState.ageInTicks * -75f));
            } else if (sleeping) {
                Direction bedOrientation = livingRenderState.bedOrientation;

                poseStack.mulPose(Axis.YP.rotationDegrees(bedOrientation != null ? RenderUtil.getDirectionAngle(bedOrientation) : rotationYaw));
                poseStack.mulPose(Axis.ZP.rotationDegrees(getDeathMaxRotation(renderState)));
                poseStack.mulPose(Axis.YP.rotationDegrees(270f));
            } else if (livingRenderState.isUpsideDown) {
                poseStack.translate(0, (livingRenderState.boundingBoxHeight + 0.1f) / nativeScale, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            }
        }
    }
}