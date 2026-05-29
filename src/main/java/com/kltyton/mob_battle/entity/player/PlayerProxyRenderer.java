package com.kltyton.mob_battle.entity.player;

import com.kltyton.mob_battle.event.DataTrackersEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PlayerProxyRenderer<T extends Player & GeoAnimatable, R extends PlayerRenderState & GeoRenderState> extends PlayerRenderer implements GeoRenderer<T, Void, R>, IGeoEntityAnimationTickInvoker<T> {
    public final PlayerReplacedEntityRenderer<T, R> playerRenderer;

    public PlayerProxyRenderer(EntityRendererProvider.Context context, boolean slim) {
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
    protected boolean shouldShowName(AbstractClientPlayer animatable, double distToCameraSq) {
        if (!((IPlayerEntityAccessor)animatable).isUsingGeckoLib()) {
            return super.shouldShowName(animatable, distToCameraSq);
        } else {
            return playerRenderer.shouldShowName((T)animatable, distToCameraSq);
        }
    }


    @Nullable
    @Override
    public RenderType getRenderType(R renderState, ResourceLocation texture) {
        return playerRenderer.getRenderType(renderState, texture);
    }

    @ApiStatus.Internal
    @Override
    public R captureDefaultRenderState(T animatable, Void relatedObject, R renderState, float partialTick) {
        return playerRenderer.captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);
    }

    @Override
    public void preRender(R renderState, PoseStack poseStack, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                          boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        playerRenderer.preRender(renderState, poseStack, model, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void adjustPositionForRender(R renderState, PoseStack poseStack, BakedGeoModel model, boolean isReRender) {
        playerRenderer.adjustPositionForRender(renderState, poseStack, model, isReRender);
    }
    @Override
    public void scaleModelForRender(R renderState, float widthScale, float heightScale, PoseStack poseStack, BakedGeoModel model, boolean isReRender) {
        playerRenderer.scaleModelForRender(renderState, widthScale, heightScale, poseStack, model, isReRender);
    }

    @ApiStatus.Internal
    @Override
    public void render(PlayerRenderState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
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
    public void actuallyRender(R renderState, PoseStack poseStack, BakedGeoModel model, @Nullable RenderType renderType,
                               MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        playerRenderer.actuallyRender(renderState, poseStack, model, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void renderFinal(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                            int packedLight, int packedOverlay, int renderColor) {
        playerRenderer.renderFinal(renderState, poseStack, model, bufferSource, buffer, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void doPostRenderCleanup() {
        playerRenderer.doPostRenderCleanup();
    }

    @Override
    public void renderRecursively(R renderState, PoseStack poseStack, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        playerRenderer.renderRecursively(renderState, poseStack, bone, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
    }

    @Deprecated
    @ApiStatus.Internal
    @Nullable
    @Override
    public R createRenderState() {
        return (R)new PlayerRenderState();
    }

    @ApiStatus.Internal
    public final R getAndUpdateRenderState(AbstractClientPlayer entity, float partialTick) {
        if (entity.getEntityData().get(DataTrackersEvent.IS_GECKO_LIB_USING)) {
            try {
                playerRenderer.createRenderState((T)entity, partialTick);
            } catch (Exception e) {
            }
        }
        return (R)super.createRenderState(entity, partialTick);
    }

    @ApiStatus.OverrideOnly
    @Override
    public void extractRenderState(AbstractClientPlayer entity, PlayerRenderState entityRenderState, float partialTick) {
        super.extractRenderState(entity, entityRenderState, partialTick);
        boolean usingGeckoLib = ((IPlayerEntityAccessor)entity).isUsingGeckoLib();
        ((IPlayerStateAccessor)entityRenderState).setUseGeckoLib(usingGeckoLib);
        if (usingGeckoLib) {
            try {
                playerRenderer.extractRenderState((T) entity, (R) entityRenderState, partialTick);
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
        playerRenderer.fireCompileRenderStateEvent(animatable, relatedObject, renderState);
    }
    @Override
    public boolean firePreRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        return playerRenderer.firePreRenderEvent(renderState, poseStack, model, bufferSource);
    }

    @Override
    public void firePostRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        playerRenderer.firePostRenderEvent(renderState, poseStack, model, bufferSource);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void mobBattle$tickGeckoAnimations(T entity, float partialTick) {
        try {
            ((IGeoEntityAnimationTickInvoker<T>) playerRenderer).mobBattle$tickGeckoAnimations(entity, partialTick);
        } catch (NullPointerException e) {
            // 忽略相机未初始化的错误，这可能在渲染器完全初始化之前发生
        }
    }
}
