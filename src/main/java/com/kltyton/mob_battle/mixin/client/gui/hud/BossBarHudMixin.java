package com.kltyton.mob_battle.mixin.client.gui.hud;

import com.kltyton.mob_battle.bossbar.CustomBossBarClientState;
import com.kltyton.mob_battle.bossbar.CustomBossBarStyle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public class BossBarHudMixin {
    @Inject(method = "reset", at = @At("HEAD"))
    private void mobBattle$clearCustomBossBars(CallbackInfo ci) {
        CustomBossBarClientState.clear();
    }

    @Inject(
            method = "drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mobBattle$renderCustomBossBar(GuiGraphics context, int x, int y, BossEvent bossBar, CallbackInfo ci) {
        CustomBossBarStyle style = CustomBossBarClientState.get(bossBar.getId()).orElse(null);
        if (style == null) {
            return;
        }

        int renderX = x - (style.renderWidth() - 182) / 2;
        context.blit(
                RenderPipelines.GUI_TEXTURED,
                style.backgroundTexture(),
                renderX,
                y,
                0.0F,
                0.0F,
                style.renderWidth(),
                style.renderHeight(),
                style.textureWidth(),
                style.textureHeight(),
                style.textureWidth(),
                style.textureHeight()
        );

        int progressWidth = Mth.lerpDiscrete(bossBar.getProgress(), 0, style.renderWidth());
        if (progressWidth > 0) {
            int progressTextureWidth = Mth.lerpDiscrete(bossBar.getProgress(), 0, style.textureWidth());
            context.blit(
                    RenderPipelines.GUI_TEXTURED,
                    style.progressTexture(),
                    renderX,
                    y,
                    0.0F,
                    0.0F,
                    progressWidth,
                    style.renderHeight(),
                    progressTextureWidth,
                    style.textureHeight(),
                    style.textureWidth(),
                    style.textureHeight()
            );
        }

        ci.cancel();
    }
}
