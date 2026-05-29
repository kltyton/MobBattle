package com.kltyton.mob_battle.entity.blueirongolem;

import com.kltyton.mob_battle.Mob_battle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.render.entity.state.IronGolemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
@Environment(EnvType.CLIENT)
public class BlueIronGolemEntityRenderer extends MobEntityRenderer<BlueIronGolemEntity, IronGolemEntityRenderState, IronGolemEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(Mob_battle.MOD_ID, "textures/entity/blue_iron_golem/blue_iron_golem.png");

    public BlueIronGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IronGolemEntityModel(context.getPart(EntityModelLayers.IRON_GOLEM)), 0.7F);
        this.addFeature(new BlueIronGolemCrackFeatureRenderer(this));
        this.addFeature(new IronGolemFlowerFeatureRenderer(this, context.getBlockRenderManager()));
    }

    public Identifier getTexture(IronGolemEntityRenderState ironGolemEntityRenderState) {
        return TEXTURE;
    }

    public IronGolemEntityRenderState createRenderState() {
        return new IronGolemEntityRenderState();
    }

    public void updateRenderState(BlueIronGolemEntity ironGolemEntity, IronGolemEntityRenderState ironGolemEntityRenderState, float f) {
        super.updateRenderState(ironGolemEntity, ironGolemEntityRenderState, f);
        ironGolemEntityRenderState.attackTicksLeft = ironGolemEntity.getAttackTicksLeft() > 0.0F ? ironGolemEntity.getAttackTicksLeft() - f : 0.0F;
        ironGolemEntityRenderState.lookingAtVillagerTicks = ironGolemEntity.getLookingAtVillagerTicks();
        ironGolemEntityRenderState.crackLevel = ironGolemEntity.getCrackLevel();
    }

    protected void setupTransforms(IronGolemEntityRenderState ironGolemEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms(ironGolemEntityRenderState, matrixStack, f, g);
        if (!(ironGolemEntityRenderState.limbSwingAmplitude < 0.01)) {
            float h = 13.0F;
            float i = ironGolemEntityRenderState.limbSwingAnimationProgress + 6.0F;
            float j = (Math.abs(i % 13.0F - 6.5F) - 3.25F) / 3.25F;
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.5F * j));
        }
    }
}
