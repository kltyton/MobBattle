package com.kltyton.mob_battle.client.render;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.client.ParticleStormClientBridge;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

@Environment(EnvType.CLIENT)
public final class ClientGeckoParticleEffects {
    private static final Map<LivingEntity, Map<String, Vec3>> LOCATOR_POSITIONS = new WeakHashMap<>();

    private ClientGeckoParticleEffects() {
    }

    public static <R extends GeoRenderState> void trackLocator(
            RenderPassInfo<R> renderPassInfo,
            LivingEntity entity,
            String locatorName,
            String... aliases
    ) {
        renderPassInfo.addLocatorPositionListener(locatorName, (worldPos, modelPos, localPos) -> {
            if (worldPos == null) {
                return;
            }

            Map<String, Vec3> positions = LOCATOR_POSITIONS.computeIfAbsent(entity, ignored -> new HashMap<>());
            Vec3 position = new Vec3(worldPos.x, worldPos.y, worldPos.z);
            positions.put(locatorName, position);
            for (String alias : aliases) {
                positions.put(alias, position);
            }
        });
    }

    public static void spawn(LivingEntity entity, String effect, String locatorName) {
        try {
            Identifier particleId = Identifier.parse(effect);
            ParticleStormClientBridge.spawnEmitter(particleId, findSpawnPosition(entity, locatorName), -1);
        } catch (RuntimeException exception) {
            Mob_battle.LOGGER.warn("Failed to spawn ParticleStorm GeckoLib keyframe effect: {}", effect, exception);
        }
    }

    private static Vec3 findSpawnPosition(LivingEntity entity, String locatorName) {
        if (locatorName != null) {
            Map<String, Vec3> positions = LOCATOR_POSITIONS.get(entity);
            if (positions != null) {
                Vec3 locatorPosition = positions.get(locatorName);
                if (locatorPosition != null) {
                    return locatorPosition;
                }
            }
        }

        return entity.position().add(0.0D, entity.getBbHeight() * 0.5D, 0.0D);
    }
}
