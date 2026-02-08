package com.kltyton.mob_battle.client.keybinding;

import com.kltyton.mob_battle.client.screen.MasterScepterScreen;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.network.packet.ShieldSpawnPayload;
import com.kltyton.mob_battle.network.packet.SummonDronePayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinding {
    public static KeyBinding keySummon;   // 默认 X
    public static KeyBinding keyAttackDroneMode; // 默认 C
    public static KeyBinding keyTreatmentDroneMode;     // 默认 Z
    public static KeyBinding keyMasterScepter;
    public static KeyBinding shieldKey;

    public static void init() {
        keySummon = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.summon_drone",           // 翻译键
                InputUtil.Type.KEYSYM,           // 键盘
                GLFW.GLFW_KEY_X,                 // 默认 X 键
                "category.mob_battle.general"         // 分类（会在按键设置界面显示）
        ));

        keyAttackDroneMode = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.attack_drone_mode",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.mob_battle.general"
        ));

        keyTreatmentDroneMode = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.treatment_drone_mode",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "category.mob_battle.general"
        ));
        keyMasterScepter = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.master_scepter",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_BACKSLASH,
                "category.mob_battle.general"
        ));
        shieldKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.shield",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.mob_battle.general"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // while ：长按持续触发（类似原版打开背包的行为）
            while (keySummon.wasPressed()) {
                ClientPlayNetworking.send(new SummonDronePayload(1));
            }
            while (keyAttackDroneMode.wasPressed()) {
                ClientPlayNetworking.send(new SummonDronePayload(2));
            }
            while (keyTreatmentDroneMode.wasPressed()) {
                ClientPlayNetworking.send(new SummonDronePayload(3));
            }
            while (keyMasterScepter.wasPressed()) {
                if (client.player != null && (client.player.getMainHandStack().isOf(ModItems.MASTER_SCEPTER) || client.player.getOffHandStack().isOf(ModItems.MASTER_SCEPTER))) {
                    client.execute(() ->
                            client.setScreen(new MasterScepterScreen())
                    );
                }
            }
            while (shieldKey.wasPressed()) {
                ClientPlayNetworking.send(new ShieldSpawnPayload());
            }
        });
    }
}
