package com.kltyton.mob_battle.mixin.entity.boss.dragon;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.boss.dragon.EnderDragonAccessor;
import com.kltyton.mob_battle.entity.boss.dragon.IEnderDragonEntityRenderStateAccessor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EnderDragonEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// ==================== 视觉模型整体放大 1.5 倍 ====================
@Mixin(EnderDragonEntityRenderer.class)
public abstract class EnderDragonEntityRendererMixin extends EntityRenderer<EnderDragonEntity, EnderDragonEntityRenderState> {
    @Unique
    private static final Identifier SHADOW_TEXTURE = Identifier.of(Mob_battle.MOD_ID, "textures/entity/ender_dragon_shadow/ender_dragon_shadow.png");
    @Unique
    private static final RenderLayer SHADOW_DRAGON_CUTOUT = RenderLayer.getEntityTranslucentEmissive(SHADOW_TEXTURE);
    @Unique
    private static final RenderLayer SHADOW_DRAGON_DECAL = RenderLayer.getEntityDecal(SHADOW_TEXTURE);
    @Unique
    private static final float VISUAL_SCALE = 1.5F;

    protected EnderDragonEntityRendererMixin(EntityRendererFactory.Context context) {
        super(context);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/render/entity/state/EnderDragonEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    shift = At.Shift.AFTER
            )
    )
    private void applyVisualScale(EnderDragonEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        // 只对活着的龙应用（死亡动画时不放大，避免爆炸效果畸形）
        if (state.ticksSinceDeath <= 0.0F) {
            matrices.scale(VISUAL_SCALE, VISUAL_SCALE, VISUAL_SCALE);
        }
    }
    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;Lnet/minecraft/client/render/entity/state/EnderDragonEntityRenderState;F)V",
            at = @At("HEAD")
    )
    private void updateRenderState(EnderDragonEntity entity, EnderDragonEntityRenderState state, float tickDelta, CallbackInfo ci) {
        // 关键点：必须处理 else 情况，将状态重置为 false
        ((IEnderDragonEntityRenderStateAccessor) state).setShadow(((EnderDragonAccessor) entity).isShadow());
    }

    @Redirect(
            method = "render(Lnet/minecraft/client/render/entity/state/EnderDragonEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;",
                    ordinal = 1
            )
    )
    private VertexConsumer applyVisualScale(VertexConsumerProvider instance, RenderLayer renderLayer, EnderDragonEntityRenderState enderDragonEntityRenderState) {
        if (((IEnderDragonEntityRenderStateAccessor) enderDragonEntityRenderState).isShadow()) {
            return instance.getBuffer(SHADOW_DRAGON_DECAL);
        } else {
            return instance.getBuffer(renderLayer);
        }
    }
    @Redirect(
            method = "render(Lnet/minecraft/client/render/entity/state/EnderDragonEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;",
                    ordinal = 2
            )
    )
    private VertexConsumer applyVisualScale_2(VertexConsumerProvider instance, RenderLayer renderLayer, EnderDragonEntityRenderState enderDragonEntityRenderState) {
        if (((IEnderDragonEntityRenderStateAccessor) enderDragonEntityRenderState).isShadow()) {
            return instance.getBuffer(SHADOW_DRAGON_CUTOUT);
        } else {
            return instance.getBuffer(renderLayer);
        }
    }
}