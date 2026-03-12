package com.kltyton.mob_battle.entity.player;

import com.kltyton.mob_battle.event.DataTrackersEvent;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.List;

public class PlayerProxyRenderer<T extends PlayerEntity & GeoAnimatable, R extends PlayerEntityRenderState & GeoRenderState> extends PlayerEntityRenderer implements GeoRenderer<T, Void, R> {
    public final PlayerReplacedEntityRenderer<T, R> playerRenderer;

    public PlayerProxyRenderer(EntityRendererFactory.Context context, boolean slim) {
        super(context, slim);
        playerRenderer = new PlayerReplacedEntityRenderer<>(context);
    }

    @Override
    public GeoModel<T> getGeoModel() {
        return playerRenderer.getGeoModel();
    }

    @Override
    public List<GeoRenderLayer<T, Void, R>> getRenderLayers() {
        return playerRenderer.getRenderLayers();
    }

    @ApiStatus.Internal
    @Override
    public long getInstanceId(T animatable, Void relatedObject) {
        return playerRenderer.getInstanceId(animatable, relatedObject);
    }

    @Override
    public int getRenderColor(T animatable, Void relatedObject, float partialTick) {
        return playerRenderer.getRenderColor(animatable, relatedObject, partialTick);
    }

    @Override
    public int getPackedOverlay(T animatable, Void relatedObject, float u, float partialTick) {
        return playerRenderer.getPackedOverlay(animatable, relatedObject, u, partialTick);
    }

    @Override
    public boolean hasLabel(AbstractClientPlayerEntity animatable, double distToCameraSq) {
        if (!((IPlayerEntityAccessor)animatable).isUsingGeckoLib()) {
            return super.hasLabel(animatable, distToCameraSq);
        } else {
            return playerRenderer.hasLabel((T)animatable, distToCameraSq);
        }
    }


    @Nullable
    @Override
    public RenderLayer getRenderType(R renderState, Identifier texture) {
        return playerRenderer.getRenderType(renderState, texture);
    }

    @ApiStatus.Internal
    @Override
    public R captureDefaultRenderState(T animatable, Void relatedObject, R renderState, float partialTick) {
        return playerRenderer.captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);
    }

    @Override
    public void preRender(R renderState, MatrixStack poseStack, BakedGeoModel model, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                          boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        playerRenderer.preRender(renderState, poseStack, model, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void adjustPositionForRender(R renderState, MatrixStack poseStack, BakedGeoModel model, boolean isReRender) {
        playerRenderer.adjustPositionForRender(renderState, poseStack, model, isReRender);
    }
    @Override
    public void scaleModelForRender(R renderState, float widthScale, float heightScale, MatrixStack poseStack, BakedGeoModel model, boolean isReRender) {
        playerRenderer.scaleModelForRender(renderState, widthScale, heightScale, poseStack, model, isReRender);
    }

    @ApiStatus.Internal
    @Override
    public void render(PlayerEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (((IPlayerStateAccessor)state).isUsingGeckoLib()) {
            try {
                playerRenderer.render((R)state, matrices, vertexConsumers, light);
            } catch (IllegalArgumentException e) {
                if (e.getMessage() != null && e.getMessage().contains("GeoRenderState that does not exist")) {
                    super.render(state, matrices, vertexConsumers, light);
                } else {
                    throw e;
                }
            }
        } else {
            super.render(state, matrices, vertexConsumers, light);
        }
    }
    @Override
    public void actuallyRender(R renderState, MatrixStack poseStack, BakedGeoModel model, @Nullable RenderLayer renderType,
                               VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        playerRenderer.actuallyRender(renderState, poseStack, model, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void renderFinal(R renderState, MatrixStack poseStack, BakedGeoModel model, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                            int packedLight, int packedOverlay, int renderColor) {
        playerRenderer.renderFinal(renderState, poseStack, model, bufferSource, buffer, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void doPostRenderCleanup() {
        playerRenderer.doPostRenderCleanup();
    }

    @Override
    public void renderRecursively(R renderState, MatrixStack poseStack, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer,
                                  boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        playerRenderer.renderRecursively(renderState, poseStack, bone, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
    }

    @Deprecated
    @ApiStatus.Internal
    @Nullable
    @Override
    public R createRenderState() {
        return (R)new PlayerEntityRenderState();
    }

    @ApiStatus.Internal
    @Override
    public final R getAndUpdateRenderState(AbstractClientPlayerEntity entity, float partialTick) {
        if (entity.getDataTracker().get(DataTrackersEvent.IS_GECKO_LIB_USING)) {
            try {
                playerRenderer.getAndUpdateRenderState((T)entity, partialTick);
            } catch (Exception e) {
            }
        }
        return (R)super.getAndUpdateRenderState(entity, partialTick);
    }

    @ApiStatus.OverrideOnly
    @Override
    public void updateRenderState(AbstractClientPlayerEntity entity, PlayerEntityRenderState entityRenderState, float partialTick) {
        super.updateRenderState(entity, entityRenderState, partialTick);
        boolean usingGeckoLib = ((IPlayerEntityAccessor)entity).isUsingGeckoLib();
        ((IPlayerStateAccessor)entityRenderState).setUseGeckoLib(usingGeckoLib);
        if (usingGeckoLib) {
            try {
                playerRenderer.updateRenderState((T) entity, (R) entityRenderState, partialTick);
            } catch (Exception e) {
            }
        }
    }
    @Override
    public void fireCompileRenderLayersEvent() {
        playerRenderer.fireCompileRenderLayersEvent();
    }

    @Override
    public void fireCompileRenderStateEvent(T animatable, Void relatedObject, R renderState) {
        playerRenderer.fireCompileRenderStateEvent((T)animatable, relatedObject, renderState);
    }
    @Override
    public boolean firePreRenderEvent(R renderState, MatrixStack poseStack, BakedGeoModel model, VertexConsumerProvider bufferSource) {
        return playerRenderer.firePreRenderEvent(renderState, poseStack, model, bufferSource);
    }

    @Override
    public void firePostRenderEvent(R renderState, MatrixStack poseStack, BakedGeoModel model, VertexConsumerProvider bufferSource) {
        playerRenderer.firePostRenderEvent(renderState, poseStack, model, bufferSource);
    }
}
