package com.kltyton.mob_battle.event.masterscepter;

import com.kltyton.mob_battle.entity.customfireball.CustomFireballEntity;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 大师权杖爆裂火球序列。
 *
 * <p>状态按 {@link MinecraftServer} 实例隔离，延时任务只保存玩家 UUID 和世界键，
 * 每次 tick 从目标服务器重新解析玩家。生命周期清理由
 * {@link MasterScepterManager} 统一调用，避免断线或停服后继续发射。</p>
 */
public final class SbBfb {
    private static final int FIREBALL_COUNT = 30;
    private static final int FIREBALL_DELAY_TICKS = 7;
    private static final Map<MinecraftServer, ServerState> SERVER_STATES = new IdentityHashMap<>();
    private static final AtomicBoolean TICK_REGISTERED = new AtomicBoolean();

    private SbBfb() {
    }

    /** 注册一次该技能的服务器 tick 回调；生命周期清理由 MasterScepterManager 负责。 */
    public static void initLifecycle() {
        if (!TICK_REGISTERED.compareAndSet(false, true)) {
            return;
        }
        ServerTickEvents.START_SERVER_TICK.register(SbBfb::tick);
    }

    /** 在指定服务器状态中尝试启动一条爆裂火球序列。 */
    static boolean tryStart(MinecraftServer server, UUID playerId, ResourceKey<Level> worldKey) {
        return stateFor(server).tryStart(playerId, worldKey, FIREBALL_COUNT);
    }

    private static void tick(MinecraftServer server) {
        ServerState state = SERVER_STATES.get(server);
        if (state == null) {
            return;
        }

        Iterator<DelayedTask> iterator = state.tasks.iterator();
        while (iterator.hasNext()) {
            DelayedTask task = iterator.next();
            if (task.delayTicks > 0) {
                task.delayTicks--;
                continue;
            }

            ServerLevel world = server.getLevel(task.worldKey);
            ServerPlayer player = server.getPlayerList().getPlayer(task.playerId);
            if (!isValidTarget(world, player)) {
                iterator.remove();
                state.activePlayers.remove(task.playerId);
                continue;
            }

            Vec3 eyePos = player.getEyePosition();
            CustomFireballEntity fireball = new CustomFireballEntity(world, player, 2.5F, true, 50.0F);
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
            world.playSound(null, eyePos.x, eyePos.y, eyePos.z,
                    SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS,
                    0.5F, 1.2F + player.getRandom().nextFloat() * 0.2F
            );

            task.remainingFireballs--;
            if (task.remainingFireballs > 0) {
                task.delayTicks = FIREBALL_DELAY_TICKS;
            } else {
                iterator.remove();
                state.activePlayers.remove(task.playerId);
            }
        }

        if (state.tasks.isEmpty()) {
            SERVER_STATES.remove(server);
        }
    }

    private static boolean isValidTarget(ServerLevel world, ServerPlayer player) {
        return world != null
                && player != null
                && player.level() == world
                && player.isAlive()
                && !player.isRemoved()
                && !player.hasDisconnected();
    }

    private static ServerState stateFor(MinecraftServer server) {
        return SERVER_STATES.computeIfAbsent(server, ignored -> new ServerState());
    }

    /** 清除指定服务器中指定玩家的延时序列，用于断线收尾。 */
    static void clearPlayer(MinecraftServer server, UUID playerId) {
        ServerState state = SERVER_STATES.get(server);
        if (state == null) {
            return;
        }
        state.clearPlayer(playerId);
        removeIfEmpty(server, state);
    }

    /** 清除指定服务器的全部延时序列，用于停服收尾。 */
    static void clearServer(MinecraftServer server) {
        SERVER_STATES.remove(server);
    }

    private static void removeIfEmpty(MinecraftServer server, ServerState state) {
        if (state.tasks.isEmpty()) {
            SERVER_STATES.remove(server);
        }
    }

    /**
     * 单个服务器的延时序列状态。该容器不保存 MinecraftServer、ServerPlayer 或 ServerLevel
     * 实例，因此不会通过任务本身延长服务器、玩家或世界的生命周期。
     */
    public static final class ServerState {
        private final Deque<DelayedTask> tasks = new ArrayDeque<>();
        private final Set<UUID> activePlayers = new HashSet<>();

        /** 尝试为玩家建立一条序列；同一服务器状态内重复注册会被拒绝。 */
        public boolean tryStart(UUID playerId, ResourceKey<Level> worldKey, int fireballs) {
            Objects.requireNonNull(playerId, "playerId");
            Objects.requireNonNull(worldKey, "worldKey");
            if (fireballs <= 0 || !activePlayers.add(playerId)) {
                return false;
            }
            tasks.addLast(new DelayedTask(playerId, worldKey, fireballs));
            return true;
        }

        /** 清除指定玩家的序列及活动标记。 */
        public void clearPlayer(UUID playerId) {
            activePlayers.remove(playerId);
            tasks.removeIf(task -> task.playerId.equals(playerId));
        }

        /** 清除该服务器状态中的全部序列。 */
        public void clear() {
            tasks.clear();
            activePlayers.clear();
        }

        /** 返回该服务器状态中玩家是否仍有活动序列。 */
        public boolean isPlayerActive(UUID playerId) {
            return activePlayers.contains(playerId);
        }

        /** 返回该服务器状态中尚未完成的序列数量。 */
        public int taskCount() {
            return tasks.size();
        }
    }

    /** 一条只含可重建引用的延时任务，不持有玩家对象。 */
    private static final class DelayedTask {
        private final UUID playerId;
        private final ResourceKey<Level> worldKey;
        private int remainingFireballs;
        private int delayTicks;

        private DelayedTask(UUID playerId, ResourceKey<Level> worldKey, int fireballs) {
            this.playerId = playerId;
            this.worldKey = worldKey;
            this.remainingFireballs = fireballs;
        }
    }
}
