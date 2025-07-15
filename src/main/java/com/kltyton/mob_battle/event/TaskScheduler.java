package com.kltyton.mob_battle.event;

import java.util.ArrayDeque;
import java.util.Queue;

public class TaskScheduler {
    private static final Queue<Runnable> tasks = new ArrayDeque<>();
    private static int currentTick = 0;

    public static void scheduleTask(int delayTicks, Runnable task) {
        tasks.add(() -> {
            if (delayTicks <= 0) {
                task.run();
            } else {
                scheduleTask(delayTicks - 1, task);
            }
        });
    }

    public static void tick() {
        currentTick++;
        while (!tasks.isEmpty()) {
            Runnable task = tasks.poll();
            task.run();
        }
    }
}
