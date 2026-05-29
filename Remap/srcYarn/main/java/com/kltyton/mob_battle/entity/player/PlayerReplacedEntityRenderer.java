package com.kltyton.mob_battle.entity.player;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.CustomBoneTextureGeoLayer;

public class PlayerReplacedEntityRenderer<T extends PlayerEntity & GeoAnimatable, R extends PlayerEntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    public PlayerReplacedEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new PlayerReplacedEntityModel<>());
        addRenderLayer(new CustomBoneTextureGeoLayer<>(this, "Head", null) {
            @Override
            protected Identifier getTextureResource(R renderState) {
                return renderState.skinTextures.texture();
            }
            @Override
            protected void createVerticesOfQuad(R renderState, GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer,
                                                float widthRatio, float heightRatio, int packedOverlay, int packedLight, int renderColor) {
                Vector3f localNormal = quad.normal();
                float targetU = 8;
                float targetV = 8;
                if (localNormal.y() > 0.5f) { // Up
                    targetU = 16; targetV = 0;
                } else if (localNormal.y() < -0.5f) { // Down
                    targetU = 24; targetV = 0;
                } else if (localNormal.z() > 0.5f) { // South
                    targetU = 24;
                } else if (localNormal.x() > 0.5f) { // East
                    targetU = 0;
                } else if (localNormal.x() < -0.5f) { // West
                    targetU = 16;
                }
                float minU = Float.MAX_VALUE, maxU = Float.MIN_VALUE;
                float minV = Float.MAX_VALUE, maxV = Float.MIN_VALUE;
                for (GeoVertex v : quad.vertices()) {
                    minU = Math.min(minU, v.texU()); maxU = Math.max(maxU, v.texU());
                    minV = Math.min(minV, v.texV()); maxV = Math.max(maxV, v.texV());
                }
                float quadWidth = maxU - minU;
                float quadHeight = maxV - minV;

                for (GeoVertex vertex : quad.vertices()) {
                    Vector3f position = vertex.position();
                    Vector4f vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0f));

                    float relativeU = quadWidth  > 0 ? (vertex.texU() - minU) / quadWidth : 0;
                    float relativeV = quadHeight > 0 ? (vertex.texV() - minV) / quadHeight : 0;

                    float finalU = (targetU + relativeU * 8) / 64f;
                    float finalV = (targetV + relativeV * 8) / 64f;

                    // 注意：最后的法线依然使用变换后的 normal，以保证光照阴影正确
                    buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), renderColor,
                            finalU, finalV,
                            packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
                }
            }
            @Override
            protected RenderLayer getRenderType(R renderState, Identifier texture) {
                return RenderLayer.getEntityTranslucent(texture);
            }
        });
    }
    @Override
    protected R createBaseRenderState(T entity) {
        return (R) new PlayerEntityRenderState();
    }
}
