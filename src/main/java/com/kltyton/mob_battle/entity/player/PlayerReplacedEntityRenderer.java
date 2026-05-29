package com.kltyton.mob_battle.entity.player;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.CustomBoneTextureGeoLayer;

public class PlayerReplacedEntityRenderer<T extends Player & GeoAnimatable, R extends PlayerRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    public PlayerReplacedEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerReplacedEntityModel<>());
        addRenderLayer(new CustomBoneTextureGeoLayer<>(this, "Head", null) {
            @Override
            protected ResourceLocation getTextureResource(R renderState) {
                return renderState.skin.texture();
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
                    buffer.addVertex(vector4f.x(), vector4f.y(), vector4f.z(), renderColor,
                            finalU, finalV,
                            packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
                }
            }
            @Override
            protected RenderType getRenderType(R renderState, ResourceLocation texture) {
                return RenderType.entityTranslucent(texture);
            }
        });
    }
    @Override
    protected R createBaseRenderState(T entity) {
        return (R) new PlayerRenderState();
    }
}
