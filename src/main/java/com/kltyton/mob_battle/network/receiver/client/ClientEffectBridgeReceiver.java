package com.kltyton.mob_battle.network.receiver.client;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.ParticleStormClientBridge;
import com.kltyton.mob_battle.network.packet.ParticleStormEmitterPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * 客户端特效桥接接收器。
 *
 * <p>负责处理 ParticleStorm 粒子发射包 {@link ParticleStormEmitterPayload}：通过可选模组桥接
 * {@link ParticleStormClientBridge} 生成粒子；桥接未加载时捕获 LinkageError 并记录警告，
 * 与拆分前行为保持一致。</p>
 */
public final class ClientEffectBridgeReceiver {

    private ClientEffectBridgeReceiver() {
    }

    /**
     * 注册 ParticleStorm 粒子发射接收器（ParticleStormEmitterPayload）。
     */
    public static void registerParticleStormEmitter() {
        ClientPlayNetworking.registerGlobalReceiver(ParticleStormEmitterPayload.ID, (payload, context) -> {
            try {
                ParticleStormClientBridge.spawnEmitter(payload.particleId(), payload.pos(), payload.entityId());
            } catch (LinkageError error) {
                Mob_battle.LOGGER.warn("ParticleStorm 客户端桥接未加载，无法生成粒子：{}", payload.particleId(), error);
            }
        });
    }
}
