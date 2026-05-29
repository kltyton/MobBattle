package com.kltyton.mob_battle.entity.littleperson.archer.littlearrow;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.ModModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

@Environment(EnvType.CLIENT)
public class StoneArrowEntityRender extends EntityRenderer<LittleArrowEntity, ArrowEntityRenderState> {
    public static final Identifier TEXTURE = Identifier.of(Mob_battle.MOD_ID, "textures/entity/projectiles/stone_arrow.png");
    public static final Identifier TIPPED_TEXTURE = Identifier.ofVanilla("textures/entity/projectiles/stone_arrow.png");
    private final StoneArrowEntityModel model;

    public StoneArrowEntityRender(EntityRendererFactory.Context context) {
        super(context);
        this.model = new StoneArrowEntityModel(context.getPart(ModModel.STONE_ARROW));
    }
    @Override
    public void render(ArrowEntityRenderState projectileEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(projectileEntityRenderState.yaw - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(projectileEntityRenderState.pitch));
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(this.getTexture(projectileEntityRenderState)));
        this.model.setAngles(projectileEntityRenderState);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(projectileEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    public void updateRenderState(LittleArrowEntity persistentProjectileEntity, ArrowEntityRenderState projectileEntityRenderState, float f) {
        super.updateRenderState(persistentProjectileEntity, projectileEntityRenderState, f);
        projectileEntityRenderState.pitch = persistentProjectileEntity.getLerpedPitch(f);
        projectileEntityRenderState.yaw = persistentProjectileEntity.getLerpedYaw(f);
        projectileEntityRenderState.shake = persistentProjectileEntity.shake - f;
        projectileEntityRenderState.tipped = persistentProjectileEntity.getColor() > 0;
    }

    protected Identifier getTexture(ArrowEntityRenderState arrowEntityRenderState) {
        return arrowEntityRenderState.tipped ? TIPPED_TEXTURE : TEXTURE;
    }

    public ArrowEntityRenderState createRenderState() {
        return new ArrowEntityRenderState();
    }
}
