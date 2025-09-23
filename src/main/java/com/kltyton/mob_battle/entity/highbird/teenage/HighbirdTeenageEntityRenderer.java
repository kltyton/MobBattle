package com.kltyton.mob_battle.entity.highbird.teenage;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.util.RenderUtil;

public class HighbirdTeenageEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<HighbirdTeenageEntity, R> {
    public HighbirdTeenageEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new HighbirdTeenageEntityModel());
    }
    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 90f;
    }
    @Override
    public int getPackedOverlay(HighbirdTeenageEntity animatable, Void relatedObject, float u, float partialTick) {
        if (!(animatable instanceof LivingEntity entity))
            return OverlayTexture.DEFAULT_UV;
        if (entity.isDead()) return OverlayTexture.DEFAULT_UV;
        return OverlayTexture.packUv(OverlayTexture.getU(u), OverlayTexture.getV(entity.hurtTime > 0));
    }
    @Override
    protected void applyRotations(R renderState, MatrixStack poseStack, float nativeScale) {
        float rotationYaw = renderState.getGeckolibData(DataTickets.ENTITY_BODY_YAW);

        if (renderState.getGeckolibData(DataTickets.IS_SHAKING))
            rotationYaw += (float)(Math.cos(renderState.age * 3.25d) * Math.PI * 0.4d);

        boolean sleeping = renderState.getGeckolibData(DataTickets.ENTITY_POSE) == EntityPose.SLEEPING;

        if (!sleeping)
            poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f - rotationYaw));

        if (renderState instanceof LivingEntityRenderState livingRenderState) {
            if (livingRenderState.usingRiptide) {
                poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f - livingRenderState.pitch));
                poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(renderState.age * -75f));
            }
            else if (sleeping) {
                Direction bedOrientation = livingRenderState.sleepingDirection;

                poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(bedOrientation != null ? RenderUtil.getDirectionAngle(bedOrientation) : rotationYaw));
                poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(getDeathMaxRotation(renderState)));
                poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270f));
            }
            else if (livingRenderState.flipUpsideDown) {
                poseStack.translate(0, (livingRenderState.height + 0.1f) / nativeScale, 0);
                poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f));
            }
        }
    }
}
