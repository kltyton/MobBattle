package com.kltyton.mob_battle.sounds.bgm.boss.dragon;

import com.kltyton.mob_battle.entity.boss.dragon.EnderDragonAccessor;
import com.kltyton.mob_battle.sounds.ModSounds;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Comparator;

public class DragonBgmHandler {
    private static final double RANGE = 1000.0;
    private static SoundInstance currentInstance = null;
    private static Identifier currentEvent = null;

    private static int scanCooldown = 0;
    private static EnderDragonEntity cachedBoss = null;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null || client.world.getRegistryKey() != World.END) {
                stop(client);
                return;
            }

            EnderDragonEntity boss = getOrUpdateBoss(client);

            // 核心判定修改：增加对死亡动画（deathTime）和血量的严格检查
            if (boss != null && boss.getHealth() > 0 && !boss.isRemoved() && boss.deathTime == 0) {

                boolean isHalfHealth = boss.getHealth() / boss.getMaxHealth() <= 0.5f;
                Identifier targetEvent = isHalfHealth ? ModSounds.DRAGON_BGM_HALF_ID : ModSounds.DRAGON_BGM_NORMAL_ID;

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

    private static EnderDragonEntity getOrUpdateBoss(MinecraftClient client) {
        // 1. 如果缓存的龙已经死了或开始播放死亡动画，立即清除缓存，不等待冷却
        if (cachedBoss != null) {
            if (cachedBoss.getWorld() != client.world || cachedBoss.isRemoved() || cachedBoss.getHealth() <= 0 || cachedBoss.deathTime > 0) {
                cachedBoss = null;
            } else if (cachedBoss.squaredDistanceTo(client.player) > RANGE * RANGE) {
                cachedBoss = null;
            }
        }

        if (cachedBoss != null) return cachedBoss;

        // 2. 只有没有缓存时才进行扫描冷却
        if (scanCooldown-- > 0) return null;
        scanCooldown = 20; // 降低到每秒扫描一次

        Box box = null;
        if (client.player != null) {
            box = client.player.getBoundingBox().expand(RANGE);
        }

        // 3. 搜索时直接过滤掉正在死亡或已经死亡的龙
        if (client.world != null) {
            cachedBoss = client.world.getEntitiesByClass(
                            EnderDragonEntity.class,
                            box,
                            e -> e.getHealth() > 0 && !((EnderDragonAccessor)e).isShadow() && e.deathTime == 0
                    ).stream()
                    .min(Comparator.comparingDouble(a -> a.squaredDistanceTo(client.player)))
                    .orElse(null);
        }

        return cachedBoss;
    }

    private static void play(MinecraftClient client, Identifier event) {
        stop(client);

        currentInstance = new PositionedSoundInstance(
                event,
                SoundCategory.MUSIC,
                1.0F, 1.0F,
                SoundInstance.createRandom(),
                true,
                0,
                SoundInstance.AttenuationType.NONE,
                0, 0, 0,
                true
        );

        client.getSoundManager().play(currentInstance);
        currentEvent = event;
    }

    private static void stop(MinecraftClient client) {
        if (currentInstance != null) {
            client.getSoundManager().stop(currentInstance);
            currentInstance = null;
            currentEvent = null;
            cachedBoss = null; // 停止音乐时顺便清空缓存，确保下次进入能重新扫描
        }
    }
}