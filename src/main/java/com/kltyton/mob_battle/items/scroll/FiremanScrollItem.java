package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.entity.customfireball.CustomSmallFireballEntity;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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

/**
 * 火焰人卷轴：启动一个四发火球的服务端延时序列。
 *
 * <p>延时状态按 {@link MinecraftServer} 实例隔离，任务只保存玩家 UUID 与世界键，
 * 每次 tick 从当前服务器的玩家列表重新解析玩家。生命周期事件负责移除断线、死亡、
 * 世界卸载和停服状态，避免把旧服务器或旧玩家继续挂在静态状态上。</p>
 */
public class FiremanScrollItem extends FireballScrollItem {
    private static final int FIREBALL_COUNT = 4;
    private static final int FIREBALL_DELAY_TICKS = 5;
    private static final Map<MinecraftServer, ServerState> SERVER_STATES = new IdentityHashMap<>();
    private static final AtomicBoolean LIFECYCLE_REGISTERED = new AtomicBoolean();

    public FiremanScrollItem(Properties settings) {
        super(settings);
    }

    /**
     * 注册卷轴所需的生命周期回调。Fabric 事件是全局对象，因此必须显式保证只装配一次。
     */
    public static void initLifecycle() {
        if (!LIFECYCLE_REGISTERED.compareAndSet(false, true)) {
            return;
        }

        ServerTickEvents.START_SERVER_TICK.register(FiremanScrollItem::tick);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                clearPlayer(server, handler.getPlayer().getUUID()));
        ServerLevelEvents.UNLOAD.register((server, level) ->
                clearWorld(server, level.dimension()));
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayer player) {
                clearPlayer(player.level().getServer(), player.getUUID());
            }
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(FiremanScrollItem::clearServer);
        ServerLifecycleEvents.SERVER_STOPPED.register(FiremanScrollItem::clearServer);
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
            CustomSmallFireballEntity fireball = new CustomSmallFireballEntity(
                    world, player, 15F, false
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

    private static void clearPlayer(MinecraftServer server, UUID playerId) {
        ServerState state = SERVER_STATES.get(server);
        if (state != null) {
            state.clearPlayer(playerId);
            if (state.tasks.isEmpty()) {
                SERVER_STATES.remove(server);
            }
        }
    }

    private static void clearWorld(MinecraftServer server, ResourceKey<Level> worldKey) {
        ServerState state = SERVER_STATES.get(server);
        if (state != null) {
            state.clearWorld(worldKey);
            if (state.tasks.isEmpty()) {
                SERVER_STATES.remove(server);
            }
        }
    }

    private static void clearServer(MinecraftServer server) {
        SERVER_STATES.remove(server);
    }

    @Override
    public InteractionResult use(Level world, net.minecraft.world.entity.player.Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(world instanceof ServerLevel serverLevel)
                || !(user instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }

        MinecraftServer server = serverLevel.getServer();
        ServerState state = stateFor(server);
        if (!state.tryStart(serverPlayer.getUUID(), serverLevel.dimension(), FIREBALL_COUNT)) {
            return InteractionResult.FAIL;
        }

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS,
                0.8F, 1.0F
        );
        user.awardStat(Stats.ITEM_USED.get(this));
        if (!user.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * 单个服务器的纯状态容器，便于直接验证服务器隔离和生命周期清理。
     * 其中不保存 MinecraftServer、Player 或 World 实例。
     */
    public static final class ServerState {
        private final Deque<DelayedTask> tasks = new ArrayDeque<>();
        private final Set<UUID> activePlayers = new HashSet<>();

        /** 尝试为玩家建立一条新的四发序列；同一状态内已有序列时不改变状态。 */
        public boolean tryStart(UUID playerId, ResourceKey<Level> worldKey, int fireballs) {
            Objects.requireNonNull(playerId, "playerId");
            Objects.requireNonNull(worldKey, "worldKey");
            if (fireballs <= 0 || !activePlayers.add(playerId)) {
                return false;
            }
            tasks.addLast(new DelayedTask(playerId, worldKey, fireballs));
            return true;
        }

        /** 清除指定玩家的序列及其活动标记，用于断线和死亡收尾。 */
        public void clearPlayer(UUID playerId) {
            activePlayers.remove(playerId);
            tasks.removeIf(task -> task.playerId.equals(playerId));
        }

        /** 清除指定世界中的全部序列，用于世界卸载收尾。 */
        public void clearWorld(ResourceKey<Level> worldKey) {
            for (Iterator<DelayedTask> iterator = tasks.iterator(); iterator.hasNext(); ) {
                DelayedTask task = iterator.next();
                if (task.worldKey.equals(worldKey)) {
                    activePlayers.remove(task.playerId);
                    iterator.remove();
                }
            }
        }

        /** 清除该服务器状态中的全部序列，用于服务器停止收尾。 */
        public void clear() {
            tasks.clear();
            activePlayers.clear();
        }

        /** 返回玩家在该服务器状态中是否仍有活动序列。 */
        public boolean isPlayerActive(UUID playerId) {
            return activePlayers.contains(playerId);
        }

        /** 返回该服务器状态中尚未完成的延时序列数量。 */
        public int taskCount() {
            return tasks.size();
        }
    }

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
