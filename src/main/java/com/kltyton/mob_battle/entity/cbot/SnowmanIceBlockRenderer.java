package com.kltyton.mob_battle.entity.cbot;

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
public class SnowmanIceBlockRenderer extends EntityRenderer<SnowmanIceBlockEntity, TippableArrowRenderState> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/projectiles/snowman_ice_block.png");
    private final SnowmanIceBlockModel model;

    public SnowmanIceBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SnowmanIceBlockModel(context.bakeLayer(ModModel.SNOWMAN_ICE_BLOCK));
    }

    @Override
    public void render(TippableArrowRenderState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        matrices.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutout(TEXTURE));
        this.model.setupAnim(state);
        this.model.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        matrices.popPose();
        super.render(state, matrices, vertexConsumers, light);
    }

    @Override
    public void extractRenderState(SnowmanIceBlockEntity entity, TippableArrowRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.xRot = entity.getXRot(tickDelta);
        state.yRot = entity.getYRot(tickDelta);
    }

    @Override
    public TippableArrowRenderState createRenderState() {
        return new TippableArrowRenderState();
    }
}
