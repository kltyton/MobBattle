package com.kltyton.mob_battle.client.hud.player;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.items.armor.presence.PresenceArmorUser;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

/** 左下角持续显示服务器确认的套装模式，客户端不自行结算弹药或冷却。 */
public final class PresenceArmorHud {
    private PresenceArmorHud() {}

    public static void init() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "presence_armor"), (graphics, delta) -> {
                    Minecraft client = Minecraft.getInstance();
                    if (client.player == null) return;
                    int state = ((PresenceArmorUser) client.player).mobBattle$getPresenceState();
                    if ((state & PresenceArmorUser.EQUIPPED) == 0) return;
                    boolean aggressive = (state & PresenceArmorUser.AGGRESSIVE) != 0;
                    Component mode = Component.translatable(aggressive
                            ? "hud.mob_battle.presence_aggressive" : "hud.mob_battle.presence_neutral");
                    Component title = Component.translatable("hud.mob_battle.presence_title");
                    Component controls = Component.translatable("hud.mob_battle.presence_controls");
                    int width = Math.min(graphics.guiWidth() - 16, Math.max(152,
                            Math.max(client.font.width(controls) + 16, client.font.width(title) + client.font.width(mode) + 28)));
                    int height = 61;
                    int x = 8;
                    // 固定依附屏幕底边；仅在窄窗口与快捷栏相交时上移一行，不依赖最大生命。
                    int bottom = (graphics.guiWidth() - 182) / 2 < x + width + 6 ? 32 : 8;
                    int y = Math.max(4, graphics.guiHeight() - bottom - height);
                    int accent = aggressive ? 0xFFFFAB72 : 0xFF81D8DE;
                    graphics.fill(x, y, x + width, y + height, 0xC9171924);
                    graphics.fill(x, y, x + 2, y + height, 0xFFAF98F5);
                    graphics.text(client.font, title, x + 8, y + 6, 0xFFF2ECFF, false);
                    int modeX = x + width - client.font.width(mode) - 8;
                    graphics.text(client.font, mode, modeX, y + 6, accent, false);
                    int remaining = state >>> PresenceArmorUser.COOLDOWN_SHIFT;
                    Component cooldown = remaining == 0 ? Component.translatable("hud.mob_battle.presence_ready")
                            : Component.literal(((remaining + 19) / 20) + "s");
                    graphics.text(client.font, Component.translatable("hud.mob_battle.presence_stomp"), x + 8, y + 21, 0xFFD3CCD9, false);
                    graphics.text(client.font, cooldown, x + width - client.font.width(cooldown) - 8, y + 21,
                            remaining == 0 ? 0xFF9BE1B4 : 0xFFBBA7F2, false);
                    graphics.fill(x + 8, y + 32, x + width - 8, y + 34, 0xFF3B3548);
                    int progress = Math.round((width - 16) * (1 - Math.min(1100, remaining) / 1100.0F));
                    graphics.fill(x + 8, y + 32, x + 8 + progress, y + 34, 0xFFBBA7F2);
                    graphics.text(client.font, Component.translatable("hud.mob_battle.presence_kick"), x + 8, y + 38, 0xFFD3CCD9, false);
                    for (int i = 0; i < 5; i++) {
                        int slotX = x + width - 61 + i * 11;
                        graphics.fill(slotX, y + 39, slotX + 8, y + 46, 0xFF4B4558);
                        if (i < (state & PresenceArmorUser.AMMO_MASK)) {
                            graphics.fill(slotX + 1, y + 40, slotX + 7, y + 45, 0xFFD5C6FF);
                        }
                    }
                    graphics.text(client.font, controls, x + 8, y + 50, 0xFF9994A5, false);
                });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && ((((PresenceArmorUser) client.player).mobBattle$getPresenceState() & PresenceArmorUser.STOMPING) != 0)) {
                client.player.setDeltaMovement(Vec3.ZERO);
            }
        });
    }
}
