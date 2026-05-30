package com.kltyton.mob_battle.client.keybinding;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.screen.MasterScepterScreen;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import com.kltyton.mob_battle.entity.player.IPlayerSkillAccessor;
import com.kltyton.mob_battle.entity.player.PlayerEntitySkill;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.network.packet.*;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinding {
    private static final KeyMapping.Category MOB_BATTLE_CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "general"));

    public static KeyMapping keySummon;   // 默认 X
    public static KeyMapping keyAttackDroneMode; // 默认 C
    public static KeyMapping keyTreatmentDroneMode;     // 默认 Z
    public static KeyMapping keyMasterScepter;
    public static KeyMapping shieldKey;
    public static KeyMapping keyZiJin_0;
    public static KeyMapping keyZiJin_1;
    public static KeyMapping keyPlayerRetreatStepRun;
    public static KeyMapping keyPlayerAttack2Run;
    public static KeyMapping keyPlayerLeftWhipRun;
    public static KeyMapping keyPlayerTopKneeRun;
    public static KeyMapping keyPlayerCollisionRun;
    public static KeyMapping keyPlayerRunCollisionRun;
    public static KeyMapping keyPlayerSmashingTheGroundRun;
    public static KeyMapping keyPlayerScrapingRun;
    public static KeyMapping keyPiglinCannonItemMode;
    //压缩盔甲套装技能
    public static KeyMapping keyCompressArmorSkill_Z;
    public static KeyMapping keyCompressArmorSkill_X;
    public static KeyMapping keyCompressArmorSkill_C;

    public static void init() {
        keySummon = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.summon_drone",           // 翻译键
                InputConstants.Type.KEYSYM,           // 键盘
                GLFW.GLFW_KEY_X,                 // 默认 X 键
                MOB_BATTLE_CATEGORY         // 分类（会在按键设置界面显示）
        ));

        keyAttackDroneMode = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.attack_drone_mode",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                MOB_BATTLE_CATEGORY
        ));

        keyTreatmentDroneMode = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.treatment_drone_mode",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                MOB_BATTLE_CATEGORY
        ));
        keyMasterScepter = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.master_scepter",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_BACKSLASH,
                MOB_BATTLE_CATEGORY
        ));
        shieldKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.shield",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                MOB_BATTLE_CATEGORY
        ));
        keyZiJin_0 = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.zi_jin_0",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                MOB_BATTLE_CATEGORY
        ));
        keyZiJin_1 = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.zi_jin_1",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                MOB_BATTLE_CATEGORY
        ));
        keyPlayerRetreatStepRun = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.retreat_step_run",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                MOB_BATTLE_CATEGORY
        ));
        keyPlayerAttack2Run = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.attack2_run",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F,
                MOB_BATTLE_CATEGORY
        ));
        keyPlayerLeftWhipRun = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.left_whip_run",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                MOB_BATTLE_CATEGORY
        ));
        keyPlayerTopKneeRun = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.top_knee_run",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                MOB_BATTLE_CATEGORY
        ));
        keyPlayerCollisionRun = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.collision_run",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                MOB_BATTLE_CATEGORY
        ));
        keyPlayerRunCollisionRun = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.run_collision_run",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                MOB_BATTLE_CATEGORY
        ));
        keyPlayerSmashingTheGroundRun = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.smashing_the_ground_run",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                MOB_BATTLE_CATEGORY
        ));
        keyPlayerScrapingRun = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.scraping_run",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_T,
                MOB_BATTLE_CATEGORY
        ));
        keyPiglinCannonItemMode = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.piglin_cannon_item_mode",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                MOB_BATTLE_CATEGORY
        ));
        keyCompressArmorSkill_Z = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.compress_armor_skill_z",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                MOB_BATTLE_CATEGORY
        ));
        keyCompressArmorSkill_X = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.compress_armor_skill_x",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                MOB_BATTLE_CATEGORY
        ));
        keyCompressArmorSkill_C = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mob_battle.compress_armor_skill_c",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                MOB_BATTLE_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }
            if (((IPlayerEntityAccessor) client.player).isUsingGeckoLib()) {
                while (keyPlayerRetreatStepRun.consumeClick()) {
                    if (((IPlayerSkillAccessor)client.player).mobBattle$canAttack("retreat_step")) {
                        PlayerEntitySkill.runRetreatStepSkill(client.player);
                    }
                    client.execute(() ->
                            ClientPlayNetworking.send(new PlayerSkillPayload("retreat_step", client.player.getId()))
                    );
                }
                while (keyPlayerAttack2Run.consumeClick()) {
                    client.execute(() ->
                            ClientPlayNetworking.send(new PlayerSkillPayload("attack2_run", client.player.getId()))
                    );
                }
                while (keyPlayerLeftWhipRun.consumeClick()) {
                    client.execute(() ->
                            ClientPlayNetworking.send(new PlayerSkillPayload("left_whip_run", client.player.getId()))
                    );
                }
                while (keyPlayerTopKneeRun.consumeClick()) {
                    client.execute(() ->
                            ClientPlayNetworking.send(new PlayerSkillPayload("top_knee_run", client.player.getId()))
                    );
                }
                while (keyPlayerCollisionRun.consumeClick()) {
                    client.execute(() ->
                            ClientPlayNetworking.send(new PlayerSkillPayload("collision_run", client.player.getId()))
                    );
                }
                while (keyPlayerRunCollisionRun.consumeClick()) {
                    client.execute(() -> {
                        ClientPlayNetworking.send(new PlayerSkillPayload("run_collision_run", client.player.getId()));
                        if (((IPlayerSkillAccessor)client.player).mobBattle$canAttack("run_collision") && client.player.isSprinting()) {
                            Vec3 lookVec = client.player.getViewVector(1.0F);
                            Vec3 velocity = new Vec3(lookVec.x, 0, lookVec.z).normalize().scale(2.5);
                            client.player.setDeltaMovement(velocity.x, client.player.getDeltaMovement().y + 0.22, velocity.z);
                            client.player.hurtMarked = true;
                        }
                    });
                }
                while (keyPlayerSmashingTheGroundRun.consumeClick()) {
                    client.execute(() ->
                            ClientPlayNetworking.send(new PlayerSkillPayload("smashing_the_ground_run", client.player.getId()))
                    );
                }
                while (keyPlayerScrapingRun.consumeClick()) {
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

            while (keySummon.consumeClick()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new SummonDronePayload(1))
                );
            }
            while (keyAttackDroneMode.consumeClick()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new SummonDronePayload(2))
                );

            }
            while (keyTreatmentDroneMode.consumeClick()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new SummonDronePayload(3))
                );
            }
            while (keyMasterScepter.consumeClick()) {
                if (client.player != null && (client.player.getMainHandItem().is(ModItems.MASTER_SCEPTER) || client.player.getOffhandItem().is(ModItems.MASTER_SCEPTER))) {
                    client.execute(() ->
                            client.setScreen(new MasterScepterScreen())
                    );
                }
            }
            while (shieldKey.consumeClick()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new ShieldSpawnPayload())
                );
            }
            while (keyZiJin_0.consumeClick()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new ZiJinPayload(0))
                );
            }
            while (keyZiJin_1.consumeClick()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new ZiJinPayload(1))
                );
            }
            while (keyCompressArmorSkill_Z.consumeClick()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new CompressArmorSkillPayload(0))
                );
            }
            while (keyCompressArmorSkill_X.consumeClick()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new CompressArmorSkillPayload(1))
                );
            }
            while (keyCompressArmorSkill_C.consumeClick()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new CompressArmorSkillPayload(2))
                );
            }
            while (keyPiglinCannonItemMode.consumeClick()) {
                client.execute(() ->
                        ClientPlayNetworking.send(new PiglinCannonModePayload())
                );
            }
        });
    }
}
