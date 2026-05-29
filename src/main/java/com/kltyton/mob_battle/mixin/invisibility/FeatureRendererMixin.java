package com.kltyton.mob_battle.mixin.invisibility;

import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HumanoidArmorLayer.class, HumanoidArmorLayer.class})
public abstract class FeatureRendererMixin<S extends EntityRenderState, M extends EntityModel<? super S>> {
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
            at = @At("HEAD"),
            cancellable = true)
    private void hideElytraIfInvisible(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, HumanoidRenderState bipedEntityRenderState, float f, float g, CallbackInfo ci) {
        if (((IModEntityRenderState)bipedEntityRenderState).isTrueInvisible()) {
            ci.cancel();
        }
    }
}
