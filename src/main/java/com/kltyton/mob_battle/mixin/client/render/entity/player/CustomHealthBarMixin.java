package com.kltyton.mob_battle.mixin.client.render.entity.player;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
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

    @Unique
    private static final Identifier HEALTH_FRAME =
            Identifier.of(Mob_battle.MOD_ID, "textures/gui/health_frame.png");

    @Unique
    private static final Identifier HEALTH_PROGRESS =
            Identifier.of(Mob_battle.MOD_ID, "textures/gui/health_progress.png");

    @Unique
    private static final int BAR_WIDTH = 224;

    @Unique
    private static final int BAR_HEIGHT = 32;

    @Unique
    private static final int PER_BOSS_OFFSET = 19;

    @Unique
    private static final int CUSTOM_BAR_SPACING = 38;

    @Unique
    private static final int BASE_Y = 0;

    @Unique
    private static final int PLAYER_HEAD_SIZE = 14;

    @Unique
    private boolean shouldUseCustomHealthBar(PlayerEntity player) {
        return ((IPlayerEntityAccessor) player).isUsingGeckoLib() || player.getMaxHealth() >= 20000;
    }

    /**
     * 仍然保留：本地玩家自己满足条件时，隐藏原版红心，改成你的自定义血条。
     */
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
        int customBarY = BASE_Y + bossCount * PER_BOSS_OFFSET;

        renderCustomHealthBar(context, player, customBarY);
        ci.cancel();
    }

    /**
     * 新增：每帧额外绘制其他玩家的“Boss 血条”。
     */
    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void mobBattle$renderOtherPlayerBossHealthBars(
            DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci
    ) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null || client.player == null) {
            return;
        }

        int bossCount = this.getBossBarHud().bossBars.size();
        int barIndex = 0;

        for (PlayerEntity player : client.world.getPlayers()) {
            // 本地玩家已经由 renderHealthBar 处理，避免重复画
            if (player == client.player) {
                continue;
            }

            if (!player.isAlive()) {
                continue;
            }

            if (!shouldUseCustomHealthBar(player)) {
                continue;
            }

            // 隐身玩家不显示
            // if (player.isInvisibleTo(client.player)) {
            //     continue;
            // }

            int barY = BASE_Y
                    + bossCount * PER_BOSS_OFFSET
                    + barIndex * CUSTOM_BAR_SPACING;

            renderCustomHealthBar(context, player, barY);
            barIndex++;
        }
    }

    @Unique
    private void renderCustomHealthBar(DrawContext context, PlayerEntity player, int barY) {
        MinecraftClient client = MinecraftClient.getInstance();

        int screenWidth = client.getWindow().getScaledWidth();
        int barX = (screenWidth - BAR_WIDTH) / 2;

        float currentHealth = player.getHealth();
        float maxH = player.getMaxHealth();
        float absorption = player.getAbsorptionAmount();

        if (maxH <= 0.0F) {
            return;
        }

        float progress = Math.min(1.0F, (currentHealth + absorption) / maxH);

        renderPlayerHead(context, player, barX + 105, barY + 9);

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                HEALTH_FRAME,
                barX,
                barY,
                0,
                0,
                BAR_WIDTH,
                BAR_HEIGHT,
                BAR_WIDTH,
                BAR_HEIGHT
        );

        int filledWidth = (int) (BAR_WIDTH * progress);

        if (filledWidth > 0) {
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    HEALTH_PROGRESS,
                    barX,
                    barY,
                    0,
                    0,
                    filledWidth,
                    BAR_HEIGHT,
                    BAR_WIDTH,
                    BAR_HEIGHT
            );
        }

        String text = player.getName().getString()
                + "  "
                + (int) currentHealth
                + " / "
                + (int) maxH;

        if (absorption > 0) {
            text += " (+" + (int) absorption + ")";
        }

        int textWidth = client.textRenderer.getWidth(text);

        context.drawText(
                client.textRenderer,
                text,
                barX + (BAR_WIDTH - textWidth) / 2,
                barY + (BAR_HEIGHT - 8) / 2,
                0xFFFFFFFF,
                true
        );
    }

    @Unique
    private void renderPlayerHead(DrawContext context, PlayerEntity player, int x, int y) {
        if (!(player instanceof AbstractClientPlayerEntity clientPlayer)) {
            return;
        }

        Identifier skinTexture = clientPlayer.getSkinTextures().texture();

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                skinTexture,
                x,
                y,
                8.0F,
                8.0F,
                PLAYER_HEAD_SIZE,
                PLAYER_HEAD_SIZE,
                8,
                8,
                64,
                64
        );
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                skinTexture,
                x,
                y,
                40.0F,
                8.0F,
                PLAYER_HEAD_SIZE,
                PLAYER_HEAD_SIZE,
                8,
                8,
                64,
                64
        );
    }
}
