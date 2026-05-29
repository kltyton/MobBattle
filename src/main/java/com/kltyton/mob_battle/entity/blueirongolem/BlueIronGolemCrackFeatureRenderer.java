package com.kltyton.mob_battle.entity.blueirongolem;

import com.google.common.collect.ImmutableMap;
import com.kltyton.mob_battle.Mob_battle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Crackiness;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class BlueIronGolemCrackFeatureRenderer extends RenderLayer<IronGolemRenderState, IronGolemModel> {
    private static final Map<Crackiness.Level, ResourceLocation> CRACK_TEXTURES = ImmutableMap.of(
            Crackiness.Level.LOW,
            ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"textures/entity/blue_iron_golem/blue_iron_golem_crackiness_low.png"),
            Crackiness.Level.MEDIUM,
            ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"textures/entity/blue_iron_golem/blue_iron_golem_crackiness_medium.png"),
            Crackiness.Level.HIGH,
            ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID,"textures/entity/blue_iron_golem/blue_iron_golem_crackiness_high.png")
    );

    public BlueIronGolemCrackFeatureRenderer(RenderLayerParent<IronGolemRenderState, IronGolemModel> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(
            PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, IronGolemRenderState ironGolemEntityRenderState, float f, float g
    ) {
        if (!ironGolemEntityRenderState.isInvisible) {
            Crackiness.Level crackLevel = ironGolemEntityRenderState.crackiness;
            if (crackLevel != Crackiness.Level.NONE) {
                ResourceLocation identifier = CRACK_TEXTURES.get(crackLevel);
                renderColoredCutoutModel(this.getParentModel(), identifier, matrixStack, vertexConsumerProvider, i, ironGolemEntityRenderState, -1);
            }
        }
    }
}
