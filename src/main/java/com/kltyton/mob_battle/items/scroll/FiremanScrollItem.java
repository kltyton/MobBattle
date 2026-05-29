package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.entity.customfireball.CustomSmallFireballEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FiremanScrollItem extends FireballScrollItem {

    // 延时任务队列（线程安全）
    private static final Queue<DelayedTask> TASK_QUEUE = new ConcurrentLinkedQueue<>();
    // 记录当前处理的任务
    private static final Set<UUID> ACTIVE_TASKS = new HashSet<>();

    public FiremanScrollItem(Properties settings) {
        super(settings);
    }

    // 延时任务数据结构
    private static class DelayedTask {
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
                CustomSmallFireballEntity fireball = new CustomSmallFireballEntity(
                        world, player, 15F
                );
                fireball.setPos(eyePos);

                Vec3 lookVec = player.getViewVector(1.0F);
                Vec3 spreadVec = lookVec.offsetRandom(player.getRandom(), 0.1F);
                float speed = 1.2F;
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
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS,
                0.8F, 1.0F
        );

        if (!world.isClientSide) {
            UUID playerId = user.getUUID();

            // 防止重复添加任务
            if (!ACTIVE_TASKS.contains(playerId)) {
                TASK_QUEUE.add(new DelayedTask(
                        playerId,
                        world.dimension(),
                        4 // 总共4颗火球
                ));
                ACTIVE_TASKS.add(playerId);
            }
        }

        user.awardStat(Stats.ITEM_USED.get(this));
        if (!user.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}