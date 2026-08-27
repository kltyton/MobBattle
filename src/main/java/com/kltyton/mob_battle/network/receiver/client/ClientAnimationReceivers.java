package com.kltyton.mob_battle.network.receiver.client;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.animation.ModPlayerAnimationClientHandler;
import com.kltyton.mob_battle.client.PalMoreClientBridge;
import com.kltyton.mob_battle.network.packet.PalMorePlayerAnimationPayload;
import com.kltyton.mob_battle.network.packet.PlayerAnimationPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;

/**
 * 客户端动画接收器。
 *
 * <p>负责处理玩家动画同步包 {@link PlayerAnimationPayload} 与 PALMR 桥接动画同步包
 * {@link PalMorePlayerAnimationPayload}。Fabric 当前回调已经位于客户端渲染线程，
 * 因此直接应用动画状态，避免重复调度。</p>
 */
public final class ClientAnimationReceivers {

    private ClientAnimationReceivers() {
    }

    /**
     * 注册玩家动画接收器（PlayerAnimationPayload）。
     */
    public static void registerPlayerAnimation() {
        // 收到同步包后，在客户端当前维度中找到目标玩家并操作其动画控制器。
        ClientPlayNetworking.registerGlobalReceiver(PlayerAnimationPayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            if (client.level == null) {
                return;
            }

            Entity entity = client.level.getEntity(payload.avatarEntityId());
            if (entity instanceof Avatar avatar) {
                if (payload.stop()) {
                    ModPlayerAnimationClientHandler.stop(avatar);
                } else {
                    ModPlayerAnimationClientHandler.play(avatar, payload.animationId());
                }
            }
        });
    }

    /**
     * 注册 PALMR 桥接动画接收器（PalMorePlayerAnimationPayload）。
     */
    public static void registerPalMorePlayerAnimation() {
        ClientPlayNetworking.registerGlobalReceiver(PalMorePlayerAnimationPayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            if (client.level == null) {
                return;
            }
            Entity entity = client.level.getEntity(payload.avatarEntityId());
            try {
                PalMoreClientBridge.playKnifeAnimation(entity, payload.animationId());
            } catch (LinkageError error) {
                Mob_battle.LOGGER.warn("PALMR 客户端桥接未加载，无法播放动画：{}", payload.animationId(), error);
            }
        });
    }
}
