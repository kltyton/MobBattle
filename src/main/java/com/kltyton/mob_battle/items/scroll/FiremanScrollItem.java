package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.entity.customfireball.CustomSmallFireballEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FiremanScrollItem extends FireballScrollItem {

    // 延时任务队列（线程安全）
    private static final Queue<DelayedTask> TASK_QUEUE = new ConcurrentLinkedQueue<>();
    // 记录当前处理的任务
    private static final Set<UUID> ACTIVE_TASKS = new HashSet<>();

    public FiremanScrollItem(Settings settings) {
        super(settings);
    }

    // 延时任务数据结构
    private static class DelayedTask {
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

    // 注册服务器tick事件（只需执行一次）
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
                CustomSmallFireballEntity fireball = new CustomSmallFireballEntity(
                        world, player, 15F
                );
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
                    task.delayTicks = 5; // 设置5 ticks延迟
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

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS,
                0.8F, 1.0F
        );

        if (!world.isClient) {
            UUID playerId = user.getUuid();

            // 防止重复添加任务
            if (!ACTIVE_TASKS.contains(playerId)) {
                TASK_QUEUE.add(new DelayedTask(
                        playerId,
                        world.getRegistryKey(),
                        4 // 总共4颗火球
                ));
                ACTIVE_TASKS.add(playerId);
            }
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        return ActionResult.SUCCESS;
    }
}