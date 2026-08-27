package com.kltyton.mob_battle.skill.server;

/**
 * 服务端技能指令的纯策略边界。
 *
 * <p>网络层先验证玩家确实在跟踪目标实体，再要求目标处于服务端已启动的技能状态。
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
        NO_ACTIVE_SKILL,
        ENTITY_NOT_DYING,
        /** 实体未声明允许出生指令，或当前不处于服务端已启动的出生流程。 */
        SPAWN_NOT_ALLOWED
    }
}
