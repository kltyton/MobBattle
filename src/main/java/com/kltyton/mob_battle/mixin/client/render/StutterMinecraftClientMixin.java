package com.kltyton.mob_battle.mixin.client.render;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.kltyton.mob_battle.effect.ModEffects;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.textures.GpuTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class StutterMinecraftClientMixin {
    private static final int STUTTER_INTERVAL_TICKS = 20 * 2;
    @Unique
    private boolean mob_battle$stutterRendering;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setPhase(Ljava/lang/String;)V", ordinal = 1))
    private void mob_battle$stutter(boolean tick, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            mob_battle$stutterRendering = false;
            return;
        }

        StatusEffectInstance effect = player.getStatusEffect(ModEffects.STUTTER_ENTRY);
        if (effect == null) {
            mob_battle$stutterRendering = false;
            return;
        }

        int freezeTicks = Math.max(1, effect.getAmplifier() + 1) * 20;
        mob_battle$stutterRendering = player.age % STUTTER_INTERVAL_TICKS < freezeTicks;
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearColorAndDepthTextures(Lcom/mojang/blaze3d/textures/GpuTexture;ILcom/mojang/blaze3d/textures/GpuTexture;D)V"))
    private boolean mob_battle$shouldClearFrame(CommandEncoder instance, GpuTexture colorTexture, int color, GpuTexture depthTexture, double depth) {
        return !mob_battle$stutterRendering;
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(Lnet/minecraft/client/render/RenderTickCounter;Z)V"))
    private boolean mob_battle$shouldRenderFrame(GameRenderer instance, RenderTickCounter tickCounter, boolean tick) {
        return !mob_battle$stutterRendering;
    }
}
