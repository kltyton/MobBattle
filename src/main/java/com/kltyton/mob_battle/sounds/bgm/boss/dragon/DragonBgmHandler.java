package com.kltyton.mob_battle.sounds.bgm.boss.dragon;

import com.kltyton.mob_battle.entity.boss.dragon.EnderDragonAccessor;
import com.kltyton.mob_battle.sounds.ModSounds;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.Comparator;

public class DragonBgmHandler {
    private static final double RANGE = 1000.0;
    private static SoundInstance currentInstance = null;
    private static ResourceLocation currentEvent = null;

    private static int scanCooldown = 0;
    private static EnderDragon cachedBoss = null;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null || client.player == null || client.level.dimension() != Level.END) {
                stop(client);
                return;
            }

            EnderDragon boss = getOrUpdateBoss(client);

            // 核心判定修改：增加对死亡动画（deathTime）和血量的严格检查
            if (boss != null && boss.getHealth() > 0 && !boss.isRemoved() && boss.deathTime == 0) {

                boolean isHalfHealth = boss.getHealth() / boss.getMaxHealth() <= 0.5f;
                ResourceLocation targetEvent = isHalfHealth ? ModSounds.DRAGON_BGM_HALF_ID : ModSounds.DRAGON_BGM_NORMAL_ID;

                // 只有当曲目真的需要切换时才处理，避免每 tick 重复调用
                if (currentEvent != targetEvent) {
                    play(client, targetEvent);
                }
            } else {
                // 如果龙死了、消失了、或者正在播死亡动画，立即停止
                if (currentInstance != null) {
                    stop(client);
                }
            }
        });
    }

    private static EnderDragon getOrUpdateBoss(Minecraft client) {
        // 1. 如果缓存的龙已经死了或开始播放死亡动画，立即清除缓存，不等待冷却
        if (cachedBoss != null) {
            if (cachedBoss.level() != client.level || cachedBoss.isRemoved() || cachedBoss.getHealth() <= 0 || cachedBoss.deathTime > 0) {
                cachedBoss = null;
            } else if (cachedBoss.distanceToSqr(client.player) > RANGE * RANGE) {
                cachedBoss = null;
            }
        }

        if (cachedBoss != null) return cachedBoss;

        // 2. 只有没有缓存时才进行扫描冷却
        if (scanCooldown-- > 0) return null;
        scanCooldown = 20; // 降低到每秒扫描一次

        AABB box = null;
        if (client.player != null) {
            box = client.player.getBoundingBox().inflate(RANGE);
        }

        // 3. 搜索时直接过滤掉正在死亡或已经死亡的龙
        if (client.level != null) {
            cachedBoss = client.level.getEntitiesOfClass(
                            EnderDragon.class,
                            box,
                            e -> e.getHealth() > 0 && !((EnderDragonAccessor)e).isShadow() && e.deathTime == 0
                    ).stream()
                    .min(Comparator.comparingDouble(a -> a.distanceToSqr(client.player)))
                    .orElse(null);
        }

        return cachedBoss;
    }

    private static void play(Minecraft client, ResourceLocation event) {
        stop(client);

        currentInstance = new SimpleSoundInstance(
                event,
                SoundSource.MUSIC,
                1.0F, 1.0F,
                SoundInstance.createUnseededRandom(),
                true,
                0,
                SoundInstance.Attenuation.NONE,
                0, 0, 0,
                true
        );

        client.getSoundManager().play(currentInstance);
        currentEvent = event;
    }

    private static void stop(Minecraft client) {
        if (currentInstance != null) {
            client.getSoundManager().stop(currentInstance);
            currentInstance = null;
            currentEvent = null;
            cachedBoss = null; // 停止音乐时顺便清空缓存，确保下次进入能重新扫描
        }
    }
}