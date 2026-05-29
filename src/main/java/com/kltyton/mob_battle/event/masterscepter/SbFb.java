package com.kltyton.mob_battle.event.masterscepter;

import com.kltyton.mob_battle.entity.customfireball.CustomFireballEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SbFb {

    // 延时任务队列（线程安全）
    public static final Queue<DelayedTask> TASK_QUEUE = new ConcurrentLinkedQueue<>();
    // 记录当前处理的任务
    public static final Set<UUID> ACTIVE_TASKS = new HashSet<>();


    // 延时任务数据结构
    public static class DelayedTask {
        final UUID playerId;
        final ResourceKey<Level> worldKey;
        int remainingFireballs;
        int delayTicks;

        DelayedTask(UUID playerId, ResourceKey<Level> worldKey, int fireballs) {
            this.playerId = playerId;
            this.worldKey = worldKey;
            this.remainingFireballs = fireballs;
            this.delayTicks = 0; // 第一颗立即发射
        }
    }
    private static void registerServerTickEvent() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            Iterator<DelayedTask> iterator = TASK_QUEUE.iterator();
            while (iterator.hasNext()) {
                DelayedTask task = iterator.next();

                // 跳过还有延迟的任务
                if (task.delayTicks > 0) {
                    task.delayTicks--;
                    continue;
                }

                // 获取玩家和世界
                ServerLevel world = server.getLevel(task.worldKey);
                Player player = world != null ? world.getPlayerByUUID(task.playerId) : null;

                // 检查有效性
                if (world == null || player == null || !player.isAlive()) {
                    iterator.remove();
                    ACTIVE_TASKS.remove(task.playerId);
                    continue;
                }

                // 发射火球
                Vec3 eyePos = player.getEyePosition();
                CustomFireballEntity fireball = new CustomFireballEntity(world, player, 1.5F, true, 15.0F);
                fireball.setPos(eyePos);

                Vec3 lookVec = player.getViewVector(1.0F);
                Vec3 spreadVec = lookVec.offsetRandom(player.getRandom(), 0.1F);
                float speed = 1.2F * 2;
                fireball.setDeltaMovement(
                        spreadVec.x * speed,
                        spreadVec.y * speed,
                        spreadVec.z * speed
                );

                world.addFreshEntity(fireball);

                // 播放音效
                world.playSound(null, eyePos.x, eyePos.y, eyePos.z,
                        SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS,
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
