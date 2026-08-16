package com.kltyton.mob_battle.client;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.animation.ModPlayerAnimationIds;
import com.kltyton.mob_battle.network.packet.PlayerSkillPayload;
import com.kltyton.playeranimationlibrarymorerotation.PalMoreAnimationController;
import com.kltyton.playeranimationlibrarymorerotation.client.PalMoreClientAnimations;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;

public final class PalMoreClientBridge {
    private static PalMoreAnimationController knifeKeyframeController;
    private static boolean controllerRegistered;
    private static boolean unavailableLogged;

    private PalMoreClientBridge() {
    }

    public static void initKnifeController() {
        try {
            if (controllerRegistered) {
                return;
            }
            PalMoreClientAnimations.registerController(knifeKeyframeController());
            controllerRegistered = true;
        } catch (LinkageError error) {
            logUnavailable("初始化 PALMR 刀类关键帧控制器失败", error);
        } catch (RuntimeException exception) {
            Mob_battle.LOGGER.warn("初始化 PALMR 刀类关键帧控制器失败。", exception);
        }
    }

    public static void playKnifeAnimation(Entity entity, Identifier animationId) {
        if (!(entity instanceof Avatar avatar)) {
            return;
        }

        try {
            initKnifeController();
            PalMoreClientAnimations.playLocal(avatar, animationId, ModPlayerAnimationIds.KNIFE_KEYFRAME_CONTROLLER);
        } catch (LinkageError error) {
            logUnavailable("播放 PALMR 刀类动画失败", error);
        } catch (RuntimeException exception) {
            Mob_battle.LOGGER.warn("播放 PALMR 刀类动画失败：{}", animationId, exception);
        }
    }

    private static PalMoreAnimationController knifeKeyframeController() {
        if (knifeKeyframeController == null) {
            knifeKeyframeController = PalMoreAnimationController.create(ModPlayerAnimationIds.KNIFE_KEYFRAME_CONTROLLER)
                    .setCustomInstructionKeyframeHandler(context -> {
                        String instruction = context.instructions().replaceAll("\\s+", "");
                        Minecraft client = Minecraft.getInstance();
                        if ("runAttack;".equals(instruction)
                                && client.player != null
                                && context.avatar() == client.player) {
                            ClientPlayNetworking.send(new PlayerSkillPayload("knife_run_attack", client.player.getId()));
                        }
                    });
        }
        return knifeKeyframeController;
    }

    private static void logUnavailable(String message, LinkageError error) {
        if (unavailableLogged) {
            return;
        }
        unavailableLogged = true;
        Mob_battle.LOGGER.warn("{}，客户端未加载 playeranimationlibrarymorerotation。", message, error);
    }
}
