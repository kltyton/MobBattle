package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.Mob_battle;
import com.geckolib.animation.state.KeyFrameEvent;
import com.geckolib.cache.animation.keyframeevent.ParticleKeyframeData;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class GeckoParticleKeyframeUtil {
    private static final String CLIENT_HANDLER_CLASS = "com.kltyton.mob_battle.client.render.ClientGeckoParticleEffects";
    private static Method clientSpawnMethod;
    private static boolean unavailableLogged;

    private GeckoParticleKeyframeUtil() {
    }

    public static void handle(LivingEntity entity, KeyFrameEvent<?, ParticleKeyframeData> event) {
        if (!entity.level().isClientSide()) {
            return;
        }

        ParticleKeyframeData data = event.keyframeData();
        String effect = data.getEffect();
        if (effect == null || effect.isBlank()) {
            return;
        }

        try {
            clientSpawnMethod().invoke(null, entity, effect, data.getLocatorName());
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException error) {
            logUnavailable(error);
        } catch (InvocationTargetException error) {
            Throwable cause = error.getCause();
            if (cause instanceof LinkageError linkageError) {
                logUnavailable(linkageError);
            } else if (cause instanceof RuntimeException runtimeException) {
                Mob_battle.LOGGER.warn("Failed to spawn GeckoLib particle keyframe effect: {}", effect, runtimeException);
            } else {
                Mob_battle.LOGGER.warn("Failed to spawn GeckoLib particle keyframe effect: {}", effect, error);
            }
        } catch (LinkageError error) {
            logUnavailable(error);
        }
    }

    private static Method clientSpawnMethod() throws ClassNotFoundException, NoSuchMethodException {
        if (clientSpawnMethod == null) {
            Class<?> handlerClass = Class.forName(CLIENT_HANDLER_CLASS);
            clientSpawnMethod = handlerClass.getMethod("spawn", LivingEntity.class, String.class, String.class);
        }
        return clientSpawnMethod;
    }

    private static void logUnavailable(Throwable error) {
        if (unavailableLogged) {
            return;
        }
        unavailableLogged = true;
        Mob_battle.LOGGER.warn("Client GeckoLib particle keyframe handler is unavailable.", error);
    }
}
