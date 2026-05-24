package com.kltyton.mob_battle.entity.littleperson.skillentity.laser;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.ModModel;
import com.kltyton.mob_battle.entity.littleperson.skillentity.SkillProjectileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.ArrowEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class LaserEntityRenderer extends EntityRenderer<SkillProjectileEntity, ArrowEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.of(Mob_battle.MOD_ID, "textures/entity/projectiles/laser.png");
    private final LaserEntityModel model;

    public LaserEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new LaserEntityModel(context.getPart(ModModel.LASER));
    }

    @Override
    public void render(ArrowEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw - 90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.pitch));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
        this.model.setAngles(state);
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        super.render(state, matrices, vertexConsumers, light);
    }

    @Override
    public void updateRenderState(SkillProjectileEntity entity, ArrowEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.pitch = entity.getLerpedPitch(tickDelta);
        state.yaw = entity.getLerpedYaw(tickDelta);
    }

    @Override
    public ArrowEntityRenderState createRenderState() {
        return new ArrowEntityRenderState();
    }
}
