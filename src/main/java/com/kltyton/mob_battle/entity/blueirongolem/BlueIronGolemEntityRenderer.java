package com.kltyton.mob_battle.entity.blueirongolem;

import com.kltyton.mob_battle.Mob_battle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.IronGolemFlowerLayer;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import net.minecraft.resources.ResourceLocation;
@Environment(EnvType.CLIENT)
public class BlueIronGolemEntityRenderer extends MobRenderer<BlueIronGolemEntity, IronGolemRenderState, IronGolemModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/blue_iron_golem/blue_iron_golem.png");

    public BlueIronGolemEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new IronGolemModel(context.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F);
        this.addLayer(new BlueIronGolemCrackFeatureRenderer(this));
        this.addLayer(new IronGolemFlowerLayer(this, context.getBlockRenderDispatcher()));
    }

    public ResourceLocation getTextureLocation(IronGolemRenderState ironGolemEntityRenderState) {
        return TEXTURE;
    }

    public IronGolemRenderState createRenderState() {
        return new IronGolemRenderState();
    }

    public void extractRenderState(BlueIronGolemEntity ironGolemEntity, IronGolemRenderState ironGolemEntityRenderState, float f) {
        super.extractRenderState(ironGolemEntity, ironGolemEntityRenderState, f);
        ironGolemEntityRenderState.attackTicksRemaining = ironGolemEntity.getAttackAnimationTick() > 0.0F ? ironGolemEntity.getAttackAnimationTick() - f : 0.0F;
        ironGolemEntityRenderState.offerFlowerTick = ironGolemEntity.getOfferFlowerTick();
        ironGolemEntityRenderState.crackiness = ironGolemEntity.getCrackiness();
    }

    protected void setupTransforms(IronGolemRenderState ironGolemEntityRenderState, PoseStack matrixStack, float f, float g) {
        super.setupRotations(ironGolemEntityRenderState, matrixStack, f, g);
        if (!(ironGolemEntityRenderState.walkAnimationSpeed < 0.01)) {
            float h = 13.0F;
            float i = ironGolemEntityRenderState.walkAnimationPos + 6.0F;
            float j = (Math.abs(i % 13.0F - 6.5F) - 3.25F) / 3.25F;
            matrixStack.mulPose(Axis.ZP.rotationDegrees(6.5F * j));
        }
    }
}
