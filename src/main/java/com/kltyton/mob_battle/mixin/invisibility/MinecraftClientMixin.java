package com.kltyton.mob_battle.mixin.invisibility;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onClientAttack(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (MinecraftClient.getInstance().player.hasStatusEffect(ModEffects.DISARM_ENTRY)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
        if (((IPlayerEntityAccessor)player).isUsingGeckoLib()) {
            if (!player.getMainHandStack().isEmpty()) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }

    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void onClientBreaking(boolean breaking, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (MinecraftClient.getInstance().player.hasStatusEffect(ModEffects.DISARM_ENTRY)) {
            ci.cancel();
        }
        if (((IPlayerEntityAccessor)player).isUsingGeckoLib()) {
            if (!player.getMainHandStack().isEmpty()) {
                ci.cancel();
            }
        }
    }
}
