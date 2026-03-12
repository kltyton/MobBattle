package com.kltyton.mob_battle.mixin.client.render.entity.player;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class CustomHealthBarMixin {

    @Shadow
    public abstract BossBarHud getBossBarHud();

    // ==================== 配置区 ====================
    @Unique
    private static final Identifier HEALTH_FRAME = Identifier.of(Mob_battle.MOD_ID, "textures/gui/health_frame.png");
    @Unique
    private static final Identifier HEALTH_PROGRESS = Identifier.of(Mob_battle.MOD_ID, "textures/gui/health_progress.png");

    // 血条宽高（必须和你纹理像素尺寸一致！）
    @Unique
    private static final int BAR_WIDTH = 224;
    @Unique
    private static final int BAR_HEIGHT = 48;
    @Unique
    private static final int PER_BOSS_OFFSET = 19;
    @Unique
    private static final int BASE_Y = 0;

    // 触发自定义血条的阈值（血量 + 吸收护盾 > 20 即 10 颗心以上就切换）
    // 你可以改成 player.getMaxHealth() > 40.0f 或者任何条件
    @Unique
    private boolean shouldUseCustomHealthBar(PlayerEntity player) {
        return ((IPlayerEntityAccessor)player).isUsingGeckoLib() || player.getMaxHealth() >= 20000;
    }
    // ===============================================

    @Inject(
            method = "renderHealthBar",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRenderHealthBar(
            DrawContext context,
            PlayerEntity player,
            int x,
            int y,
            int lines,
            int regeneratingHeartIndex,
            float maxHealth,
            int lastHealth,
            int health,
            int absorption,
            boolean blinking,
            CallbackInfo ci
    ) {
        if (!shouldUseCustomHealthBar(player)) {
            return;
        }
        int bossCount = this.getBossBarHud().bossBars.size();
        int offset = bossCount * PER_BOSS_OFFSET;

        // 最终 y 位置（顶部从 12 开始 + 所有 Boss 条占用的空间）
        int customBarY = BASE_Y + offset;
        // 取消原版红心渲染
        renderCustomHealthBar(context, player, customBarY);
        ci.cancel();
    }

    @Unique
    private void renderCustomHealthBar(DrawContext context, PlayerEntity player, int barY) {
        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();
        // 屏幕顶部中央（类似 Boss 血条位置，可自行改 y 值避开其他 HUD）
        int barX = (screenWidth - BAR_WIDTH) / 2;
        float currentHealth = player.getHealth();
        float maxH = player.getMaxHealth();
        float absorption = player.getAbsorptionAmount();

        // 进度（包含吸收护盾，超出 100% 也显示满）
        float progress = Math.min(1.0f, (currentHealth + absorption) / maxH);

        // 1. 绘制外框
        context.drawTexture(RenderPipelines.GUI_TEXTURED, HEALTH_FRAME, barX, barY, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);

        // 2. 绘制内部进度条（从左向右填充）
        int filledWidth = (int) (BAR_WIDTH * progress);
        if (filledWidth > 0) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, HEALTH_PROGRESS, barX, barY, 0, 0, filledWidth, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
        }

        String text = (int) currentHealth + " / " + (int) maxH;
        if (absorption > 0) text += " (+" + (int) absorption + ")";
        int textWidth = client.textRenderer.getWidth(text);
        context.drawText(
            client.textRenderer, text,
            barX + (BAR_WIDTH - textWidth) / 2,
            barY + BAR_HEIGHT + 4,
            0xFFFFFF, false
        );
    }
}
