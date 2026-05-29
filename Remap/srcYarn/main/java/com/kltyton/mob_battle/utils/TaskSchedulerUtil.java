package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.Mob_battle;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskSchedulerUtil {
    private static final List<ScheduledTask> tasks = new ArrayList<>();

    static {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<ScheduledTask> it = tasks.iterator();
            while (it.hasNext()) {
                ScheduledTask task = it.next();
                task.ticksLeft--;
                if (task.ticksLeft <= 0) {
                    try {
                        task.runnable.run();
                    } catch (Exception e) {
                        Mob_battle.LOGGER.error("运行计划任务时出错。");
                        throw new RuntimeException(e);
                    }
                    it.remove();
                }
            }
        });
    }

    public static void runLater(int ticksDelay, Runnable runnable) {
        if (ticksDelay < 0) ticksDelay = 0;
        tasks.add(new ScheduledTask(ticksDelay, runnable));
    }

    private static class ScheduledTask {
        int ticksLeft;
        final Runnable runnable;

        ScheduledTask(int ticksLeft, Runnable runnable) {
            this.ticksLeft = ticksLeft;
            this.runnable = runnable;
        }
    }
}


