package com.kltyton.mob_battle.client.keybinding;

import com.kltyton.mob_battle.client.screen.MasterScepterScreen;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import com.kltyton.mob_battle.entity.player.IPlayerSkillAccessor;
import com.kltyton.mob_battle.entity.player.PlayerEntitySkill;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.network.packet.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinding {
    public static KeyBinding keySummon;   // 默认 X
    public static KeyBinding keyAttackDroneMode; // 默认 C
    public static KeyBinding keyTreatmentDroneMode;     // 默认 Z
    public static KeyBinding keyMasterScepter;
    public static KeyBinding shieldKey;
    public static KeyBinding keyZiJin;
    public static KeyBinding keyPlayerRetreatStepRun;
    public static KeyBinding keyPlayerAttack2Run;
    public static KeyBinding keyPlayerLeftWhipRun;
    public static KeyBinding keyPlayerTopKneeRun;
    public static KeyBinding keyPlayerCollisionRun;
    public static KeyBinding keyPlayerRunCollisionRun;
    public static KeyBinding keyPlayerSmashingTheGroundRun;
    public static KeyBinding keyPlayerScrapingRun;
    public static KeyBinding keyPiglinCannonItemMode;

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
        keyZiJin = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.zi_jin",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.mob_battle.general"
        ));
        keyPlayerRetreatStepRun = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.retreat_step_run",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.mob_battle.general"
        ));
        keyPlayerAttack2Run = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.attack2_run",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F,
                "category.mob_battle.general"
        ));
        keyPlayerLeftWhipRun = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.left_whip_run",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.mob_battle.general"
        ));
        keyPlayerTopKneeRun = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.top_knee_run",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.mob_battle.general"
        ));
        keyPlayerCollisionRun = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.collision_run",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.mob_battle.general"
        ));
        keyPlayerRunCollisionRun = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.run_collision_run",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.mob_battle.general"
        ));
        keyPlayerSmashingTheGroundRun = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.smashing_the_ground_run",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                "category.mob_battle.general"
        ));
        keyPlayerScrapingRun = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.scraping_run",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_T,
                "category.mob_battle.general"
        ));
        keyPiglinCannonItemMode = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob_battle.piglin_cannon_item_mode",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.mob_battle.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }
            if (((IPlayerEntityAccessor) client.player).isUsingGeckoLib()) {
                while (keyPlayerRetreatStepRun.wasPressed()) {
                    if (((IPlayerSkillAccessor)client.player).mobBattle$canAttack("retreat_step")) {
                        PlayerEntitySkill.runRetreatStepSkill(client.player);
                    }
                    client.execute(() ->
                            ClientPlayNetworking.send(new PlayerSkillPayload("retreat_step", client.player.getId()))
                    );
                }
                while (keyPlayerAttack2Run.wasPressed()) {
                    client.execute(() ->
                            ClientPlayNetworking.send(new PlayerSkillPayload("attack2_run", client.player.getId()))
                    );
                }
                while (keyPlayerLeftWhipRun.wasPressed()) {
                    client.execute(() ->
                            ClientPlayNetworking.send(new PlayerSkillPayload("left_whip_run", client.player.getId()))
                    );
                }
                while (keyPlayerTopKneeRun.wasPressed()) {
                    client.execute(() ->
                            ClientPlayNetworking.send(new PlayerSkillPayload("top_knee_run", client.player.getId()))
                    );
                }
                while (keyPlayerCollisionRun.wasPressed()) {
                    client.execute(() ->
                            ClientPlayNetworking.send(new PlayerSkillPayload("collision_run", client.player.getId()))
                    );
                }
                while (keyPlayerRunCollisionRun.wasPressed()) {
                    client.execute(() -> {
                        ClientPlayNetworking.send(new PlayerSkillPayload("run_collision_run", client.player.getId()));
                        if (((IPlayerSkillAccessor)client.player).mobBattle$canAttack("run_collision") && client.player.isSprinting()) {
                            Vec3d lookVec = client.player.getRotationVec(1.0F);
                            Vec3d velocity = new Vec3d(lookVec.x, 0, lookVec.z).normalize().multiply(2.5);
                            client.player.setVelocity(velocity.x, client.player.getVelocity().y + 0.22, velocity.z);
                            client.player.velocityDirty = true;
                        }
                    });
                }
                while (keyPlayerSmashingTheGroundRun.wasPressed()) {
                    client.execute(() ->
                            ClientPlayNetworking.send(new PlayerSkillPayload("smashing_the_ground_run", client.player.getId()))
                    );
                }
                while (keyPlayerScrapingRun.wasPressed()) {
                    client.execute(() ->
                            ClientPlayNetworking.send(new PlayerSkillPayload("scraping_run", client.player.getId()))
                    );
                }
                return;
            }

            // while ：长按持续触发（类似原版打开背包的行为）
/*            while (keyAttackDroneMode.wasPressed()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new SummonDronePayload(2))
                );
                一个 Tick连点了 3 下，if(wasPressed)是3个tick执行三次操作，while(wasPressed)就是1个tick执行三次操作,if(isPressed)就是油门
            }*/

            while (keySummon.wasPressed()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new SummonDronePayload(1))
                );
            }
            while (keyAttackDroneMode.wasPressed()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new SummonDronePayload(2))
                );

            }
            while (keyTreatmentDroneMode.wasPressed()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new SummonDronePayload(3))
                );
            }
            while (keyMasterScepter.wasPressed()) {
                if (client.player != null && (client.player.getMainHandStack().isOf(ModItems.MASTER_SCEPTER) || client.player.getOffHandStack().isOf(ModItems.MASTER_SCEPTER))) {
                    client.execute(() ->
                            client.setScreen(new MasterScepterScreen())
                    );
                }
            }
            while (shieldKey.wasPressed()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new ShieldSpawnPayload())
                );
            }
            while (keyZiJin.wasPressed()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new ZiJinPayload())
                );
            }
            while (keyPiglinCannonItemMode.wasPressed()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new PiglinCannonModePayload())
                );
            }
        });
    }
}
