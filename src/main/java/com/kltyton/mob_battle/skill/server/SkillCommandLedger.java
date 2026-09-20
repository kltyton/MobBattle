package com.kltyton.mob_battle.skill.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 服务端活动技能会话的影响命令账本。
 *
 * <p>账本只记录当前会话中已经消费的影响命令。生命周期命令不进入账本，
 * 因为它们是流程控制信号而不是一次命中；会话结束、实体卸载或服务器停止时
 * 必须清理记录，避免下一次技能继承旧会话状态。
 */
public final class SkillCommandLedger {
    private static final Map<MinecraftServer, SkillCommandLedger> SERVER_LEDGERS = new IdentityHashMap<>();

    private final Map<UUID, Set<String>> consumedCommands = new HashMap<>();
    private final Set<UUID> activeSessions = new HashSet<>();

    /**
     * 创建一个独立账本；测试可直接使用，服务器运行时通过静态入口按服务器实例隔离。
     */
    public SkillCommandLedger() {
    }

    /**
     * 观察实体技能状态的边沿；从 inactive 进入 active 时建立全新会话。
     */
    public void observeSession(UUID entityId, boolean active) {
        if (!active) {
            clearEntity(entityId);
            return;
        }
        if (activeSessions.add(entityId)) {
            consumedCommands.remove(entityId);
        }
    }

    /**
     * 消费一个影响命令；同一实体会话内相同命令第二次调用返回 {@code false}。
     * 生命周期命令不占用影响命令名额，始终返回 {@code true}。
     */
    public boolean consumeImpactCommand(UUID entityId, String command) {
        if (SkillRequestPolicy.isLifecycleCommand(command)) {
            return true;
        }
        return consumedCommands
                .computeIfAbsent(entityId, ignored -> new HashSet<>())
                .add(command);
    }

    /**
     * 实体未识别命令时释放预占，避免未知命令阻塞当前会话。
     */
    public void releaseImpactCommand(UUID entityId, String command) {
        if (SkillRequestPolicy.isLifecycleCommand(command)) {
            return;
        }
        Set<String> commands = consumedCommands.get(entityId);
        if (commands != null && commands.remove(command) && commands.isEmpty()) {
            consumedCommands.remove(entityId);
        }
    }

    /**
     * 清理实体的活动会话及其已消费命令。
     */
    public void clearEntity(UUID entityId) {
        activeSessions.remove(entityId);
        consumedCommands.remove(entityId);
    }

    private void clearAll() {
        activeSessions.clear();
        consumedCommands.clear();
    }

    public static void observeSession(MinecraftServer server, Entity entity, boolean active) {
        ledger(server).observeSession(entity.getUUID(), active);
    }

    public static boolean consumeImpactCommand(MinecraftServer server, Entity entity, String command) {
        return ledger(server).consumeImpactCommand(entity.getUUID(), command);
    }

    public static void releaseImpactCommand(MinecraftServer server, Entity entity, String command) {
        ledger(server).releaseImpactCommand(entity.getUUID(), command);
    }

    public static void clearEntity(MinecraftServer server, Entity entity) {
        SkillCommandLedger current = SERVER_LEDGERS.get(server);
        if (current != null) {
            current.clearEntity(entity.getUUID());
        }
    }

    /**
     * 停服时丢弃服务器级账本对象，避免静态集合跨存档保留实体 UUID。
     */
    public static void clearServer(MinecraftServer server) {
        SkillCommandLedger current = SERVER_LEDGERS.remove(server);
        if (current != null) {
            current.clearAll();
        }
    }

    private static SkillCommandLedger ledger(MinecraftServer server) {
        return SERVER_LEDGERS.computeIfAbsent(server, ignored -> new SkillCommandLedger());
    }
}
