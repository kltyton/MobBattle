package com.kltyton.mob_battle.client;

import com.kltyton.mob_battle.Mob_battle;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public final class ParticleStormClientBridge {
    private static boolean unavailableLogged;

    private ParticleStormClientBridge() {
    }

    public static void spawnEmitter(Identifier particleId, Vec3 pos, int entityId) {
        try {
            Minecraft client = Minecraft.getInstance();
            if (client.level == null) {
                return;
            }

            Identifier resolvedParticleId = PSGameClient.LOADER.resolveParticleId(particleId);
            if (resolvedParticleId == null) {
                Mob_battle.LOGGER.warn("ParticleStorm 粒子不存在或未加载：{}", particleId);
                return;
            }

            ParticleEmitter emitter = new ParticleEmitter(client.level, pos, resolvedParticleId);
            if (entityId > 0) {
                Entity attached = client.level.getEntity(entityId);
                if (attached != null) {
                    emitter.attachEntity(attached);
                }
            }
            PSGameClient.LOADER.addEmitter(emitter, false);
        } catch (LinkageError error) {
            logUnavailable("生成 ParticleStorm 发射器失败", error);
        } catch (RuntimeException exception) {
            Mob_battle.LOGGER.warn("生成 ParticleStorm 发射器失败：{}", particleId, exception);
        }
    }

    private static void logUnavailable(String message, LinkageError error) {
        if (unavailableLogged) {
            return;
        }
        unavailableLogged = true;
        Mob_battle.LOGGER.warn("{}，客户端未加载 particlestorm。", message, error);
    }
}
