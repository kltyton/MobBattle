package com.kltyton.mob_battle.mixin.entity.boss.dragon;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.boss.dragon.EnderDragonAccessor;
import com.kltyton.mob_battle.entity.boss.dragon.IEnderDragonEntityRenderStateAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// ==================== 视觉模型整体放大 1.5 倍 ====================
@Mixin(EnderDragonRenderer.class)
public abstract class EnderDragonEntityRendererMixin extends EntityRenderer<EnderDragon, EnderDragonRenderState> {
    @Unique
    private static final ResourceLocation SHADOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/entity/ender_dragon_shadow/ender_dragon_shadow.png");
    @Unique
    private static final RenderType SHADOW_DRAGON_CUTOUT = RenderType.entityTranslucentEmissive(SHADOW_TEXTURE);
    @Unique
    private static final RenderType SHADOW_DRAGON_DECAL = RenderType.entityDecal(SHADOW_TEXTURE);
    @Unique
    private static final float VISUAL_SCALE = 1.5F;

    protected EnderDragonEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
                    shift = At.Shift.AFTER
            )
    )
    private void applyVisualScale(EnderDragonRenderState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        // 只对活着的龙应用（死亡动画时不放大，避免爆炸效果畸形）
        if (state.deathTime <= 0.0F) {
            matrices.scale(VISUAL_SCALE, VISUAL_SCALE, VISUAL_SCALE);
        }
    }
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;F)V",
            at = @At("HEAD")
    )
    private void extractRenderState(EnderDragon entity, EnderDragonRenderState state, float tickDelta, CallbackInfo ci) {
        // 关键点：必须处理 else 情况，将状态重置为 false
        ((IEnderDragonEntityRenderStateAccessor) state).setShadow(((EnderDragonAccessor) entity).isShadow());
    }

    @Redirect(
            method = "render(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
                    ordinal = 1
            )
    )
    private VertexConsumer applyVisualScale(MultiBufferSource instance, RenderType renderLayer, EnderDragonRenderState enderDragonEntityRenderState) {
        if (((IEnderDragonEntityRenderStateAccessor) enderDragonEntityRenderState).isShadow()) {
            return instance.getBuffer(SHADOW_DRAGON_DECAL);
        } else {
            return instance.getBuffer(renderLayer);
        }
    }
    @Redirect(
            method = "render(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
                    ordinal = 2
            )
    )
    private VertexConsumer applyVisualScale_2(MultiBufferSource instance, RenderType renderLayer, EnderDragonRenderState enderDragonEntityRenderState) {
        if (((IEnderDragonEntityRenderStateAccessor) enderDragonEntityRenderState).isShadow()) {
            return instance.getBuffer(SHADOW_DRAGON_CUTOUT);
        } else {
            return instance.getBuffer(renderLayer);
        }
    }
}