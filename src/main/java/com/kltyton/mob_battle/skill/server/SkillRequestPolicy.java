package com.kltyton.mob_battle.skill.server;

/**
 * 服务端技能指令的纯策略边界。
 *
 * <p>网络层先验证玩家确实在跟踪目标实体，再校验已知的玩家 owner，最后要求目标处于服务端已启动的技能状态。
 * {@code kill}/{@code die} 是死亡动画完成信号，只允许作用于已经死亡或濒死的实体。
 * {@code spawn} 是出生动画完成信号，属于实体专属指令：只有实体自身通过
 * {@code SkillEntity.canAcceptSpawnCommand()} 声明当前处于“服务端已进入出生流程”的
 * 可达合法状态时才放行，禁止把出生指令泛化到所有实体。实体是否认识具体指令仍由
 * {@code SkillEntity.handleSkillPayload} 裁决，避免维护第二份技能名表。
 */
public final class SkillRequestPolicy {
    public static final int MAX_COMMAND_LENGTH = 64;

    private SkillRequestPolicy() {
    }

    /**
     * 根据网络边界已经采集的原始事实裁决请求，不访问世界且不会产生副作用。
     */
    public static Result decide(
            boolean senderTracksEntity,
            boolean entityHasActiveSkill,
            boolean entityIsDeadOrDying,
            boolean entityAllowsSpawnCommand,
            String command
    ) {
        return decide(senderTracksEntity, true, entityHasActiveSkill, entityIsDeadOrDying,
                entityAllowsSpawnCommand, command);
    }

    /**
     * 根据网络边界采集的事实裁决技能请求；已知玩家 owner 时由调用方传入实际归属结果。
     */
    public static Result decide(
            boolean senderTracksEntity,
            boolean senderIsKnownPlayerOwner,
            boolean entityHasActiveSkill,
            boolean entityIsDeadOrDying,
            boolean entityAllowsSpawnCommand,
            String command
    ) {
        if (command == null || command.isBlank()) {
            return new Result(Decision.INVALID_COMMAND);
        }
        if (command.length() > MAX_COMMAND_LENGTH) {
            return new Result(Decision.COMMAND_TOO_LONG);
        }
        if (!isCommandShapeValid(command)) {
            return new Result(Decision.INVALID_COMMAND);
        }
        if (!senderTracksEntity) {
            return new Result(Decision.ENTITY_NOT_TRACKED);
        }
        if (!senderIsKnownPlayerOwner) {
            return new Result(Decision.NOT_OWNER);
        }
        if (isDeathCompletion(command)) {
            return new Result(entityIsDeadOrDying ? Decision.ACCEPTED : Decision.ENTITY_NOT_DYING);
        }
        if (isSpawnCompletion(command)) {
            if (!entityAllowsSpawnCommand) {
                return new Result(Decision.SPAWN_NOT_ALLOWED);
            }
            return new Result(entityHasActiveSkill ? Decision.ACCEPTED : Decision.NO_ACTIVE_SKILL);
        }
        return new Result(entityHasActiveSkill ? Decision.ACCEPTED : Decision.NO_ACTIVE_SKILL);
    }

    private static boolean isDeathCompletion(String command) {
        return "kill".equals(command) || "die".equals(command);
    }

    private static boolean isSpawnCompletion(String command) {
        return "spawn".equals(command);
    }

    /**
     * 返回不应占用影响命令账本的生命周期/流程控制命令。
     * {@code *_stop} 保留已有多阶段技能的结束信号语义。
     */
    public static boolean isLifecycleCommand(String command) {
        return "stop".equals(command)
                || "stop_ai".equals(command)
                || "start_ai".equals(command)
                || "kill".equals(command)
                || "die".equals(command)
                || "spawn".equals(command)
                || command != null && command.endsWith("_stop");
    }

    /**
     * 返回会结束当前活动技能会话、需要清理命令账本的生命周期命令。
     */
    public static boolean endsSkillSession(String command) {
        return "stop".equals(command) || "kill".equals(command) || "die".equals(command);
    }

    private static boolean isCommandShapeValid(String command) {
        for (int index = 0; index < command.length(); index++) {
            char character = command.charAt(index);
            if ((character < 'a' || character > 'z')
                    && (character < '0' || character > '9')
                    && character != '_') {
                return false;
            }
        }
        return true;
    }

    /**
     * 策略结果；网络层可直接用 {@link #accepted()} 分支，并将原因写入调试日志。
     */
    public record Result(Decision decision) {
        public boolean accepted() {
            return decision == Decision.ACCEPTED;
        }
    }

    /**
     * 可观测的拒绝原因，便于测试和问题定位。
     */
    public enum Decision {
        ACCEPTED,
        INVALID_COMMAND,
        COMMAND_TOO_LONG,
        ENTITY_NOT_TRACKED,
        NOT_OWNER,
        NO_ACTIVE_SKILL,
        ENTITY_NOT_DYING,
        /** 实体未声明允许出生指令，或当前不处于服务端已启动的出生流程。 */
        SPAWN_NOT_ALLOWED
    }
}
