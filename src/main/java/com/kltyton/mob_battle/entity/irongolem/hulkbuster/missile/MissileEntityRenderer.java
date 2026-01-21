package com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.ModModel;
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

public class MissileEntityRenderer extends EntityRenderer<MissileEntity, ArrowEntityRenderState> {
    public static final Identifier TEXTURE = Identifier.of(Mob_battle.MOD_ID, "textures/entity/projectiles/missile.png");
    public static final Identifier TIPPED_TEXTURE = Identifier.ofVanilla("textures/entity/projectiles/missile.png");
    private final MissileEntityModel model;

    public MissileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new MissileEntityModel(context.getPart(ModModel.MISSILE));
    }
    @Override
    public void render(ArrowEntityRenderState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(state.pitch));
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(this.getTexture(state)));
        // 确保你的 setAngles 内部没有会累加旋转的操作
        this.model.setAngles(state);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);

        matrixStack.pop();
        super.render(state, matrixStack, vertexConsumerProvider, i);
    }

    public void updateRenderState(MissileEntity persistentProjectileEntity, ArrowEntityRenderState projectileEntityRenderState, float f) {
        super.updateRenderState(persistentProjectileEntity, projectileEntityRenderState, f);
        projectileEntityRenderState.pitch = persistentProjectileEntity.getLerpedPitch(f);
        projectileEntityRenderState.yaw = persistentProjectileEntity.getLerpedYaw(f);
    }

    protected Identifier getTexture(ArrowEntityRenderState arrowEntityRenderState) {
        return arrowEntityRenderState.tipped ? TIPPED_TEXTURE : TEXTURE;
    }

    public ArrowEntityRenderState createRenderState() {
        return new ArrowEntityRenderState();
    }
}