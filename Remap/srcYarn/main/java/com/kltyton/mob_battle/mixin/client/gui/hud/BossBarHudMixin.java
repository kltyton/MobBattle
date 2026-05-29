package com.kltyton.mob_battle.mixin.client.gui.hud;

import com.kltyton.mob_battle.bossbar.CustomBossBarClientState;
import com.kltyton.mob_battle.bossbar.CustomBossBarStyle;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
    @Inject(method = "clear", at = @At("HEAD"))
    private void mobBattle$clearCustomBossBars(CallbackInfo ci) {
        CustomBossBarClientState.clear();
    }

    @Inject(
            method = "renderBossBar(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/entity/boss/BossBar;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mobBattle$renderCustomBossBar(DrawContext context, int x, int y, BossBar bossBar, CallbackInfo ci) {
        CustomBossBarStyle style = CustomBossBarClientState.get(bossBar.getUuid()).orElse(null);
        if (style == null) {
            return;
        }

        int renderX = x - (style.renderWidth() - 182) / 2;
        context.drawTexture(
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

        int progressWidth = MathHelper.lerpPositive(bossBar.getPercent(), 0, style.renderWidth());
        if (progressWidth > 0) {
            int progressTextureWidth = MathHelper.lerpPositive(bossBar.getPercent(), 0, style.textureWidth());
            context.drawTexture(
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
