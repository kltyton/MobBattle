package com.kltyton.mob_battle.mixin.invisibility;

import com.kltyton.mob_battle.accessor.IModEntityRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class HeldItemFeatureRendererMixin<S extends EntityRenderState, M extends EntityModel<? super S>> {

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/ArmedEntityRenderState;FF)V",
            at = @At("HEAD"),
            cancellable = true)
    private void hideElytraIfInvisible(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ArmedEntityRenderState bipedEntityRenderState, float f, float g, CallbackInfo ci) {
        if (((IModEntityRenderState)bipedEntityRenderState).isTrueInvisible()) {
            ci.cancel();
        }
    }
}
