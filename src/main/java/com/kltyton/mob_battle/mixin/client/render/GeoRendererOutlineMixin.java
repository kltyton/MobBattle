package com.kltyton.mob_battle.mixin.client.render;

import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GeoRenderer.class, remap = false)
public interface GeoRendererOutlineMixin {
    @Inject(method = "submitRenderTasks", at = @At("RETURN"))
    private void mobBattle$submitVisibleEntityOutline(RenderPassInfo<?> renderPassInfo,
                                                       OrderedSubmitNodeCollector renderTasks,
                                                       RenderType renderType,
                                                       CallbackInfo ci) {
        if (!(renderPassInfo.renderState() instanceof EntityRenderState entityState)
                || entityState.outlineColor == 0
                || renderType == null
                || renderPassInfo.model().isMissingno()) {
            return;
        }

        @SuppressWarnings("rawtypes")
        GeoRenderer renderer = renderPassInfo.renderer();
        @SuppressWarnings("unchecked")
        Identifier texture = renderer.getTextureLocation((GeoRenderState) renderPassInfo.renderState());
        RenderType outlineType = RenderTypes.outline(texture);
        if (renderType == outlineType) {
            return;
        }

        int packedLight = renderPassInfo.packedLight();
        int packedOverlay = renderPassInfo.packedOverlay();
        int outlineColor = entityState.outlineColor;
        renderTasks.submitCustomGeometry(renderPassInfo.poseStack(), outlineType, (pose, vertexConsumer) -> {
            PoseStack poseStack = renderPassInfo.poseStack();
            poseStack.pushPose();
            poseStack.last().set(pose);
            renderPassInfo.renderPosed(() ->
                    renderPassInfo.model().render(renderPassInfo, vertexConsumer, packedLight, packedOverlay, outlineColor));
            poseStack.popPose();
        });
    }
}
