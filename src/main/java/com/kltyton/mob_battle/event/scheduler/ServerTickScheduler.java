package com.kltyton.mob_battle.event.scheduler;

import com.kltyton.mob_battle.Mob_battle;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 服务器线程计划任务调度器，替代旧的全局 tick 任务工具类。
 *
 * <p>旧实现每 tick 复制整个任务列表并逐个递减剩余 tick，复杂度为 O(n) 且伴随列表复制，
 * 全局静态列表还会跨存档残留。本实现改为：
 * <ul>
 *   <li>按 {@link MinecraftServer} 实例隔离状态（{@link IdentityHashMap}），
 *       {@code SERVER_STOPPING}/{@code SERVER_STOPPED} 时清理，避免跨存档残留；</li>
 *   <li>每个实例使用按（到期 tick，全局序号）排序的 {@link PriorityQueue}，
 *       同到期任务保持入队顺序；</li>
 *   <li>{@link #schedule} 从非服务器线程调用时只做一次 {@code server.execute}，
 *       把“入队”切回服务器线程，队列只被服务器线程读写，异步调用方无需自建锁；</li>
 *   <li>任务只在该服务器的 {@link ServerTickEvents#END_SERVER_TICK} 阶段执行，
 *       世界写入始终位于服务器线程；</li>
 *   <li>单个任务抛出的 {@link RuntimeException} 记录中文上下文后跳过，
 *       不中断其余任务；{@link Error} 不捕获，保持默认崩溃行为。</li>
 * </ul>
 *
 * <p>时间语义：{@code delayTicks <= 1}（含负数钳制）的任务在调度后的下一个可执行
 * END tick 执行，即调度时 tick 数 + 1 的 END；因此任务执行期间新入队的 0/1 tick 任务
 * 得到 {@code dueTick = 当前tick + 1 > 当前tick}，不会在同一 tick 内被递归执行。
 * {@code delayTicks = d >= 2} 的任务在 d 个 tick 后的 END 执行。
 *
 * <p>复杂度：schedule 为 O(log n)（一次入堆）；每个 END tick 只弹出到期任务，
 * 共 k 个到期任务时为 O(k log n)，未到期任务不参与遍历。
 */
public final class ServerTickScheduler {
    /**
     * 全局单调序号，仅在服务器线程入队时自增；
     * 保证同到期 tick 内按实际入队顺序执行，抵消 PriorityQueue 非稳定排序。
     */
    private static final AtomicLong SEQUENCE = new AtomicLong();

    /**
     * 按服务器实例隔离的调度状态。
     * 键使用 IdentityHashMap：MinecraftServer 未重写 equals，身份比较已满足需求，
     * 且避免任何潜在 equals 实现差异；状态只由对应服务器自身的线程读写。
     */
    private static final Map<MinecraftServer, ServerState> STATES = new IdentityHashMap<>();

    private ServerTickScheduler() {
    }

    static {
        ServerTickEvents.END_SERVER_TICK.register(ServerTickScheduler::tick);
        // 两个停止事件都清理：STOPPING 之后仍可能有入队（清理旧状态），
        // STOPPED 兜底清除全部残留，确保跨存档不残留任务。
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerTickScheduler::clear);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerTickScheduler::clear);
    }

    /**
     * 延迟 {@code delayTicks} 个 tick 后在服务器线程执行 {@code runnable}。
     *
     * <p>若调用方不在服务器线程，只执行一次 {@code server.execute} 将入队切回服务器线程，
     * 任务本身不会在 execute 回调里直接运行，仍等该服务器的 END_SERVER_TICK。
     *
     * @param server     目标服务器实例，决定任务在哪个服务器的 END tick 执行
     * @param delayTicks 延迟 tick 数；{@code <= 1} 时在下一次可执行 END tick 执行
     * @param runnable   到期待执行的任务
     * @throws NullPointerException 当 {@code server} 或 {@code runnable} 为 null 时
     */
    public static void schedule(MinecraftServer server, int delayTicks, Runnable runnable) {
        Objects.requireNonNull(server, "server 不能为 null");
        Objects.requireNonNull(runnable, "runnable 不能为 null");
        if (!server.isSameThread()) {
            // 异步调用方：只做一次 execute 将“入队”切回服务器线程；
            // 任务本身仍只在 END_SERVER_TICK 阶段执行，不会在 execute 里直接运行。
            server.execute(() -> enqueue(server, delayTicks, runnable));
            return;
        }
        enqueue(server, delayTicks, runnable);
    }

    /** 服务器线程内入队：计算到期 tick 并压入该服务器对应的优先队列。 */
    private static void enqueue(MinecraftServer server, int delayTicks, Runnable runnable) {
        // delay <= 1（含负数钳制）统一为下一次可执行 END tick（当前 tick + 1）；
        // 任务执行时新入队的 0/1 tick 任务得到 dueTick > 当前 tick，
        // 因此不会在同 tick 内被弹出递归执行。
        int safeDelay = Math.max(delayTicks, 1);
        int dueTick = server.getTickCount() + safeDelay;
        ServerState state = STATES.computeIfAbsent(server, key -> new ServerState());
        state.queue.add(new ScheduledTask(dueTick, SEQUENCE.incrementAndGet(), runnable));
    }

    /** END_SERVER_TICK 回调：弹出并执行全部到期任务。 */
    private static void tick(MinecraftServer server) {
        ServerState state = STATES.get(server);
        if (state == null || state.queue.isEmpty()) {
            return;
        }
        int currentTick = server.getTickCount();
        while (!state.queue.isEmpty() && state.queue.peek().dueTick <= currentTick) {
            ScheduledTask task = state.queue.poll();
            try {
                task.runnable.run();
            } catch (RuntimeException e) {
                // 单个特效/技能任务异常只影响该任务本身；
                // 记录中文上下文后继续执行其余任务，不让一个任务崩掉整个服务器。
                // Error 不捕获，保持默认崩溃行为。
                Mob_battle.LOGGER.error(
                        "服务器计划任务执行异常，任务已跳过。到期tick={}，当前tick={}",
                        task.dueTick, currentTick, e);
            }
        }
    }

    /** 服务器停止时清除该实例的全部待执行任务，避免跨存档残留。 */
    private static void clear(MinecraftServer server) {
        STATES.remove(server);
    }

    /** 单个服务器实例的调度状态。 */
    private static final class ServerState {
        /**
         * 按（到期 tick，序号）排序；序号保证同到期任务按入队顺序执行。
         */
        final PriorityQueue<ScheduledTask> queue = new PriorityQueue<>(
                Comparator.comparingInt((ScheduledTask task) -> task.dueTick)
                        .thenComparingLong(task -> task.sequence));
    }

    /** 一条待执行任务。 */
    private static final class ScheduledTask {
        /** 到期 tick：服务器 getTickCount() 达到该值时任务可执行。 */
        final int dueTick;
        /** 入队序号，用于同到期任务保持插入顺序。 */
        final long sequence;
        /** 到期待执行的任务体。 */
        final Runnable runnable;

        ScheduledTask(int dueTick, long sequence, Runnable runnable) {
            this.dueTick = dueTick;
            this.sequence = sequence;
            this.runnable = runnable;
        }
    }
}
