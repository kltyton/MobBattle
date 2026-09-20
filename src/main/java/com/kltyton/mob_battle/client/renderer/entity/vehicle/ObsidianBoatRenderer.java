package com.kltyton.mob_battle.client.renderer.entity.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

/** 黑曜石船使用客户提供的实体纹理，沿用原版船模型与水面遮罩。 */
public final class ObsidianBoatRenderer extends AbstractBoatRenderer {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("mob_battle",
            "textures/entity/boat/obsidian_boat.png");
    private final Model.Simple waterPatchModel;
    private final EntityModel<BoatRenderState> model;

    public ObsidianBoatRenderer(EntityRendererProvider.Context context) {
        super(context, TEXTURE);
        this.waterPatchModel = new Model.Simple(
                context.bakeLayer(ModelLayers.BOAT_WATER_PATCH), ignored -> RenderTypes.waterMask());
        this.model = new BoatModel(context.bakeLayer(ModelLayers.OAK_BOAT));
    }

    @Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }

    @Override
    protected void submitTypeAdditions(BoatRenderState state, PoseStack poseStack,
                                       SubmitNodeCollector collector, int lightCoords) {
        if (!state.isUnderWater) {
            collector.submitModel(this.waterPatchModel, Unit.INSTANCE, poseStack, this.texture,
                    lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        }
    }
}
