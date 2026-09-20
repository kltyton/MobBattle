package com.kltyton.mob_battle.skill.server;

import org.jetbrains.annotations.Nullable;

/**
 * 玩家技能命中指令与服务端已启动动画的对应策略。
 */
public final class PlayerSkillRequestPolicy {
    private PlayerSkillRequestPolicy() {
    }

    /**
     * 玩家接收器实际支持的完整命令白名单；未知命令不得进入状态判断或战斗日志。
     */
    public static boolean isKnownCommand(String command) {
        if (command == null) {
            return false;
        }
        return switch (command) {
            case "attack", "attack2", "attack2_run",
                    "left_whip", "left_whip_run",
                    "top_knee", "upper_hook", "top_knee_run",
                    "collision_run", "collision_start", "collision_end",
                    "run_collision_run", "run_collision",
                    "smashing_the_ground_run", "run_jump", "smashing_the_ground",
                    "scraping", "scraping_run", "scraping_attack", "scraping_end",
                    "retreat_step", "knife_run_attack", "stop", "can_move" -> true;
            default -> false;
        };
    }

    @Nullable
    public static String requiredActiveSkill(String command) {
        if (!isKnownCommand(command)) {
            return null;
        }
        return switch (command) {
            case "attack" -> "attack";
            case "attack2" -> "attack2";
            case "left_whip" -> "left_whip";
            case "top_knee", "upper_hook" -> "top_knee";
            case "collision_start", "collision_end" -> "collision";
            case "run_collision" -> "run_collision";
            case "run_jump", "smashing_the_ground", "can_move" -> "smashing_the_ground";
            case "scraping", "scraping_attack", "scraping_end" -> "scraping";
            default -> null;
        };
    }

    public static boolean allows(String command, @Nullable String activeSkill) {
        return allows(command, activeSkill != null, activeSkill);
    }

    /**
     * 判断命令是否仍属于服务端确认中的当前技能生命周期。
     *
     * <p>仅有 activeSkill 字符串不足以证明技能仍然活跃；同时检查
     * {@code hasActiveSkill}，避免停止流程留下的旧状态重新放行客户端命令。
     */
    public static boolean allows(
            String command,
            boolean hasActiveSkill,
            @Nullable String activeSkill
    ) {
        if (!isKnownCommand(command)) {
            return false;
        }
        if ("stop".equals(command)) {
            return hasActiveSkill && activeSkill != null;
        }
        String required = requiredActiveSkill(command);
        return required == null || (hasActiveSkill && required.equals(activeSkill));
    }

    /**
     * 返回服务端允许接受 {@code stop} 的最早 tick。
     *
     * <p>数值取玩家动画完整时长换算后的 tick，并保留 2 tick 网络抖动余量；
     * 因此 stop 仍不能在动画早期解锁移动，但正常动画结束时不会因为少量传输
     * 延迟而永久锁住玩家。未知技能使用 1 tick 的保守默认值，真正的技能启动
     * 仍必须由服务端 {@code runAttack} 记录。
     */
    public static int minimumStopTicks(@Nullable String activeSkill) {
        if (activeSkill == null) {
            return 1;
        }
        return switch (activeSkill) {
            case "attack", "attack2", "left_whip" -> 18;
            case "retreat_step" -> 11;
            case "top_knee" -> 28;
            case "collision" -> 88;
            case "run_collision" -> 28;
            case "smashing_the_ground" -> 38;
            case "scraping" -> 48;
            default -> 1;
        };
    }

    /**
     * 判断 stop 是否同时满足活动技能、有效启动记录和服务端最早结束 tick。
     */
    public static boolean allowsStop(
            boolean hasActiveSkill,
            @Nullable String activeSkill,
            long serverTick,
            long earliestStopTick
    ) {
        return hasActiveSkill
                && activeSkill != null
                && serverTick >= 0L
                && earliestStopTick >= 0L
                && serverTick >= earliestStopTick;
    }

    /**
     * 返回会产生一次性命中副作用的命令；启动、阶段控制和收尾命令不在此集合中。
     */
    public static boolean isHitCommand(String command) {
        return switch (command) {
            case "attack", "attack2", "left_whip", "top_knee", "upper_hook",
                    "run_collision", "smashing_the_ground", "scraping_attack" -> true;
            default -> false;
        };
    }
}
