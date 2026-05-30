package com.kltyton.mob_battle.mixin.client.render;

import com.kltyton.mob_battle.effect.ModEffects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class StutterMinecraftClientMixin {
    private static final int STUTTER_INTERVAL_TICKS = 20 * 2;
    @Unique
    private boolean mob_battle$stutterRendering;

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;setErrorSection(Ljava/lang/String;)V", ordinal = 1))
    private void mob_battle$stutter(boolean tick, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) {
            mob_battle$stutterRendering = false;
            return;
        }

        MobEffectInstance effect = player.getEffect(ModEffects.STUTTER_ENTRY);
        if (effect == null) {
            mob_battle$stutterRendering = false;
            return;
        }

        int freezeTicks = Math.max(1, effect.getAmplifier() + 1) * 20;
        mob_battle$stutterRendering = player.tickCount % STUTTER_INTERVAL_TICKS < freezeTicks;
    }

    @Inject(method = "renderFrame", at = @At("HEAD"), cancellable = true)
    private void mob_battle$skipStutteredFrame(boolean advanceGameTime, CallbackInfo ci) {
        if (mob_battle$stutterRendering) {
            ci.cancel();
        }
    }
}
