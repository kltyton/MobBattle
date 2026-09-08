package com.kltyton.mob_battle.mixin.client.render.entity.player;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.hud.player.CustomHealthBarLayout;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class CustomHealthBarMixin {

    @Shadow
    public abstract BossHealthOverlay getBossOverlay();

    @Unique
    private static final Identifier HEALTH_FRAME =
            Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/gui/health_frame.png");

    @Unique
    private static final Identifier HEALTH_PROGRESS =
            Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "textures/gui/health_progress.png");

    @Unique
    private boolean shouldUseCustomHealthBar(Player player) {
        return ((IPlayerEntityAccessor) player).isUsingGeckoLib() || player.getMaxHealth() >= 20000;
    }

    /**
     * 仍然保留：本地玩家自己满足条件时，隐藏原版红心，改成你的自定义血条。
     */
    @Inject(
            method = "extractHearts",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRenderHealthBar(
            GuiGraphicsExtractor context,
            Player player,
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

        int bossCount = this.getBossOverlay().events.size();
        CustomHealthBarLayout.Layout layout = CustomHealthBarLayout.forBar(context.guiWidth(), bossCount, 0);

        renderCustomHealthBar(context, player, layout);
        ci.cancel();
    }

    /**
     * 新增：每帧额外绘制其他玩家的“Boss 血条”。
     */
    @Inject(
            method = "extractRenderState",
            at = @At("TAIL")
    )
    private void mobBattle$renderOtherPlayerBossHealthBars(
            GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci
    ) {
        Minecraft client = Minecraft.getInstance();

        if (client.level == null || client.player == null) {
            return;
        }

        int bossCount = this.getBossOverlay().events.size();
        int barIndex = shouldUseCustomHealthBar(client.player) ? 1 : 0;

        for (Player player : client.level.players()) {
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

            CustomHealthBarLayout.Layout layout =
                    CustomHealthBarLayout.forBar(context.guiWidth(), bossCount, barIndex);

            renderCustomHealthBar(context, player, layout);
            barIndex++;
        }
    }

    @Unique
    private void renderCustomHealthBar(
            GuiGraphicsExtractor context, Player player, CustomHealthBarLayout.Layout layout
    ) {
        Minecraft client = Minecraft.getInstance();

        float currentHealth = player.getHealth();
        float maxH = player.getMaxHealth();
        float absorption = player.getAbsorptionAmount();

        if (maxH <= 0.0F) {
            return;
        }

        float progress = Math.min(1.0F, (currentHealth + absorption) / maxH);

        // 绘制顺序固定为 frame -> progress -> head -> text，头像框和头像不能被后续血条覆盖。
        context.blit(
                RenderPipelines.GUI_TEXTURED,
                HEALTH_FRAME,
                layout.barX(),
                layout.barY(),
                0,
                0,
                CustomHealthBarLayout.BAR_WIDTH,
                CustomHealthBarLayout.BAR_HEIGHT,
                CustomHealthBarLayout.BAR_WIDTH,
                CustomHealthBarLayout.BAR_HEIGHT
        );

        int filledWidth = (int) (CustomHealthBarLayout.BAR_WIDTH * progress);

        if (filledWidth > 0) {
            context.blit(
                    RenderPipelines.GUI_TEXTURED,
                    HEALTH_PROGRESS,
                    layout.barX(),
                    layout.barY(),
                    0,
                    0,
                    filledWidth,
                    CustomHealthBarLayout.BAR_HEIGHT,
                    CustomHealthBarLayout.BAR_WIDTH,
                    CustomHealthBarLayout.BAR_HEIGHT
            );
        }

        renderPlayerHead(context, player, layout.headX(), layout.headY());

        String playerName = fitText(
                client.font,
                player.getName().getString(),
                layout.leftTextWidth()
        );
        String healthText = formatHealthText(
                client.font,
                currentHealth,
                maxH,
                absorption,
                layout.rightTextWidth()
        );

        context.text(
                client.font,
                playerName,
                layout.leftTextX(client.font.width(playerName)),
                layout.textY(),
                0xFFFFFFFF,
                true
        );
        context.text(
                client.font,
                healthText,
                layout.rightTextX(client.font.width(healthText)),
                layout.textY(),
                0xFFFFFFFF,
                true
        );
    }

    @Unique
    private static String fitText(Font font, String text, int maxWidth) {
        return font.plainSubstrByWidth(text, maxWidth);
    }

    @Unique
    private static String formatHealthText(
            Font font, float currentHealth, float maxHealth, float absorption, int maxWidth
    ) {
        String baseText = (int) currentHealth + " / " + (int) maxHealth;
        if (!(absorption > 0.0F)) {
            return fitText(font, baseText, maxWidth);
        }

        String absorptionText = " (+" + (int) absorption + ")";
        int absorptionWidth = font.width(absorptionText);
        if (absorptionWidth >= maxWidth) {
            return fitText(font, absorptionText, maxWidth);
        }

        return fitText(font, baseText, maxWidth - absorptionWidth) + absorptionText;
    }

    @Unique
    private void renderPlayerHead(GuiGraphicsExtractor context, Player player, int x, int y) {
        if (!(player instanceof AbstractClientPlayer clientPlayer)) {
            return;
        }

        PlayerFaceExtractor.extractRenderState(context, clientPlayer.getSkin(), x, y, 16);
    }
}
