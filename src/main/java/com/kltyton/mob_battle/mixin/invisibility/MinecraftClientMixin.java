package com.kltyton.mob_battle.mixin.invisibility;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void onClientAttack(CallbackInfoReturnable<Boolean> cir) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (Minecraft.getInstance().player.hasEffect(ModEffects.DISARM_ENTRY)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
        if (((IPlayerEntityAccessor)player).isUsingGeckoLib()) {
            if (!player.getMainHandItem().isEmpty()) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void onClientBreaking(boolean breaking, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (Minecraft.getInstance().player.hasEffect(ModEffects.DISARM_ENTRY)) {
            ci.cancel();
        }
        if (((IPlayerEntityAccessor)player).isUsingGeckoLib()) {
            if (!player.getMainHandItem().isEmpty()) {
                ci.cancel();
            }
        }
    }
}
