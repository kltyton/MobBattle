package com.kltyton.mob_battle.event.masterscepter;

import com.kltyton.mob_battle.entity.customfireball.CustomFireballEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SbBfb {

    // 延时任务队列（线程安全）
    public static final Queue<DelayedTask> TASK_QUEUE = new ConcurrentLinkedQueue<>();
    // 记录当前处理的任务
    public static final Set<UUID> ACTIVE_TASKS = new HashSet<>();


    // 延时任务数据结构
    public static class DelayedTask {
        final UUID playerId;
        final RegistryKey<World> worldKey;
        int remainingFireballs;
        int delayTicks;

        DelayedTask(UUID playerId, RegistryKey<World> worldKey, int fireballs) {
            this.playerId = playerId;
            this.worldKey = worldKey;
            this.remainingFireballs = fireballs;
            this.delayTicks = 0; // 第一颗立即发射
        }
    }
    private static void registerServerTickEvent() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            Iterator<SbBfb.DelayedTask> iterator = TASK_QUEUE.iterator();
            while (iterator.hasNext()) {
                SbBfb.DelayedTask task = iterator.next();

                // 跳过还有延迟的任务
                if (task.delayTicks > 0) {
                    task.delayTicks--;
                    continue;
                }

                // 获取玩家和世界
                ServerWorld world = server.getWorld(task.worldKey);
                PlayerEntity player = world != null ? world.getPlayerByUuid(task.playerId) : null;

                // 检查有效性
                if (world == null || player == null || !player.isAlive()) {
                    iterator.remove();
                    ACTIVE_TASKS.remove(task.playerId);
                    continue;
                }

                // 发射火球
                Vec3d eyePos = player.getEyePos();
                CustomFireballEntity fireball = new CustomFireballEntity(world, player, 2.5F, true, 50.0F);
                fireball.setPosition(eyePos);

                Vec3d lookVec = player.getRotationVec(1.0F);
                Vec3d spreadVec = lookVec.addRandom(player.getRandom(), 0.1F);
                float speed = 1.2F;
                fireball.setVelocity(
                        spreadVec.x * speed,
                        spreadVec.y * speed,
                        spreadVec.z * speed
                );

                world.spawnEntity(fireball);

                // 播放音效
                world.playSound(null, eyePos.x, eyePos.y, eyePos.z,
                        SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS,
                        0.5F, 1.2F + player.getRandom().nextFloat() * 0.2F
                );

                // 准备下一次发射
                task.remainingFireballs--;
                if (task.remainingFireballs > 0) {
                    task.delayTicks = 7; // 设置5 ticks延迟
                } else {
                    iterator.remove();
                    ACTIVE_TASKS.remove(task.playerId);
                }
            }
        });
    }

    // 初始化事件注册（使用静态块确保只注册一次）
    static {
        registerServerTickEvent();
    }
}

