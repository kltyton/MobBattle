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
                    Component mode = Component.translatable((state & PresenceArmorUser.AGGRESSIVE) != 0
                            ? "hud.mob_battle.presence_aggressive" : "hud.mob_battle.presence_neutral");
                    var lines = new java.util.ArrayList<>(client.font.split(Component.translatable("hud.mob_battle.presence_mode", mode,
                            state & PresenceArmorUser.AMMO_MASK), Math.max(1, graphics.guiWidth() - 12)));
                    lines.addAll(client.font.split(Component.translatable("hud.mob_battle.presence_cooldown",
                            ((state >>> PresenceArmorUser.COOLDOWN_SHIFT) + 19) / 20), Math.max(1, graphics.guiWidth() - 12)));
                    int heartRows = Math.max(1, (int) Math.ceil((client.player.getMaxHealth() + client.player.getAbsorptionAmount()) / 20.0));
                    int heartHeight = 39 + (heartRows - 1) * Math.max(3, 12 - heartRows);
                    int y = Math.max(6, graphics.guiHeight() - heartHeight - lines.size() * client.font.lineHeight - 6);
                    for (var line : lines) {
                        graphics.text(client.font, line, 6, y, 0xFFFFFFFF, true);
                        y += client.font.lineHeight;
                    }
                });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && ((((PresenceArmorUser) client.player).mobBattle$getPresenceState() & PresenceArmorUser.STOMPING) != 0)) {
                client.player.setDeltaMovement(Vec3.ZERO);
            }
        });
    }
}
