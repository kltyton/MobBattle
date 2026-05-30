package com.kltyton.mob_battle.entity.player;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.GeoQuad;
import com.geckolib.cache.model.GeoVertex;
import com.geckolib.constant.DefaultAnimations;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.CustomBoneTextureGeoLayer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PlayerReplacedEntityRenderer<T extends Player & GeoAnimatable, R extends AvatarRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    public PlayerReplacedEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerReplacedEntityModel<>());
        withRenderLayer(new CustomBoneTextureGeoLayer<>(this, "Head", null) {
            @Override
            protected Identifier getTextureResource(R renderState) {
                return renderState.skin != null ? renderState.skin.body().texturePath() : super.getTextureResource(renderState);
            }

            @Override
            protected void renderQuad(GeoQuad quad, Matrix4f pose, Vector3f normal, VertexConsumer vertexConsumer,
                                      int packedLight, int packedOverlay, int renderColor, float widthRatio, float heightRatio) {
                Vector3f localNormal = quad.normalVec();
                float targetU = 8.0F;
                float targetV = 8.0F;

                if (localNormal.y() > 0.5F) {
                    targetU = 16.0F;
                    targetV = 0.0F;
                } else if (localNormal.y() < -0.5F) {
                    targetU = 24.0F;
                    targetV = 0.0F;
                } else if (localNormal.z() > 0.5F) {
                    targetU = 24.0F;
                } else if (localNormal.x() > 0.5F) {
                    targetU = 0.0F;
                } else if (localNormal.x() < -0.5F) {
                    targetU = 16.0F;
                }

                float minU = Float.POSITIVE_INFINITY;
                float maxU = Float.NEGATIVE_INFINITY;
                float minV = Float.POSITIVE_INFINITY;
                float maxV = Float.NEGATIVE_INFINITY;
                for (GeoVertex vertex : quad.vertices()) {
                    minU = Math.min(minU, vertex.texU());
                    maxU = Math.max(maxU, vertex.texU());
                    minV = Math.min(minV, vertex.texV());
                    maxV = Math.max(maxV, vertex.texV());
                }

                float quadWidth = maxU - minU;
                float quadHeight = maxV - minV;
                for (GeoVertex vertex : quad.vertices()) {
                    Vector4f vector4f = pose.transform(new Vector4f(vertex.posX(), vertex.posY(), vertex.posZ(), 1.0F));
                    float relativeU = quadWidth > 0 ? (vertex.texU() - minU) / quadWidth : 0.0F;
                    float relativeV = quadHeight > 0 ? (vertex.texV() - minV) / quadHeight : 0.0F;
                    float finalU = (targetU + relativeU * 8.0F) / 64.0F;
                    float finalV = (targetV + relativeV * 8.0F) / 64.0F;

                    vertexConsumer.addVertex(vector4f.x(), vector4f.y(), vector4f.z(), renderColor,
                            finalU, finalV, packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
                }
            }

            @Override
            protected RenderType getRenderType(R renderState, Identifier texture) {
                return RenderTypes.entityTranslucent(texture);
            }
        });
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        DefaultAnimations.hardcodedHeadRotation(renderPassInfo, snapshots, "Head");
    }

    @Override
    public R createRenderState(T animatable, Void relatedObject) {
        return (R) new AvatarRenderState();
    }
}
