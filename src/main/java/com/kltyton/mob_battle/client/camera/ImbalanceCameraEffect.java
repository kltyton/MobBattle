package com.kltyton.mob_battle.client.camera;

import com.kltyton.mob_battle.client.camera.imbalance.ImbalanceCameraPolicy;
import com.kltyton.mob_battle.effect.ModEffects;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.player.LocalPlayer;

/**
 * 失衡效果的纯客户端视角表现，不参与服务端命中或效果裁决。
 */
public final class ImbalanceCameraEffect {
    private static LocalPlayer trackedPlayer;
    private static final ImbalanceCameraPolicy.State CAMERA_STATE = new ImbalanceCameraPolicy.State();

    private ImbalanceCameraEffect() {
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            LocalPlayer player = client.player;
            if (player == null) {
                restoreCamera(trackedPlayer);
                trackedPlayer = null;
                return;
            }

            if (trackedPlayer != player) {
                restoreCamera(trackedPlayer);
                trackedPlayer = player;
            }

            boolean active = !client.isPaused()
                    && ImbalanceCameraPolicy.shouldDisturb(
                    player.hasEffect(ModEffects.IMBALANCE_ENTRY), player.isFallFlying());
            if (!active) {
                restoreCamera(player);
                return;
            }

            CAMERA_STATE.captureOriginalPitch(player.getXRot());
            float currentYaw = finiteOrZero(player.getYRot());
            float baseYaw = finiteOrZero(currentYaw - CAMERA_STATE.appliedYawOffset());
            float nextYawOffset = ImbalanceCameraPolicy.randomHorizontalOffset(player.getRandom().nextFloat());
            float nextYaw = finiteOrZero(baseYaw + nextYawOffset);
            player.setYRot(nextYaw);
            CAMERA_STATE.setAppliedYawOffset(nextYaw - baseYaw);
            player.setXRot(ImbalanceCameraPolicy.nextPitch(player.getXRot()));
        });
    }

    private static void restoreCamera(LocalPlayer player) {
        if (player != null) {
            if (CAMERA_STATE.hasOriginalPitch()) {
                player.setXRot(CAMERA_STATE.originalPitch());
            }
            removeAppliedYawOffset(player);
        }
        CAMERA_STATE.clear();
    }

    private static void removeAppliedYawOffset(LocalPlayer player) {
        float appliedYawOffset = CAMERA_STATE.appliedYawOffset();
        if (appliedYawOffset == 0.0F) {
            return;
        }

        float currentYaw = finiteOrZero(player.getYRot());
        player.setYRot(finiteOrZero(currentYaw - appliedYawOffset));
    }

    private static float finiteOrZero(float value) {
        return Float.isFinite(value) ? value : 0.0F;
    }
}
