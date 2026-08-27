package com.kltyton.mob_battle.skill.api;

/**
 * 具有技能状态和服务端技能指令入口的实体统一契约。
 *
 * <p>旧实体会分批迁移到本接口。默认实现刻意采用“拒绝并不改变状态”的保守行为，
 * 因此尚未迁移的实体不会因为接口升级而意外执行客户端请求。
 */
public interface SkillEntity {
    /**
     * 返回实体当前是否具备主动施放技能的基础条件。
     */
    boolean canSkill();

    /**
     * 返回实体是否正处于由服务端发起的技能流程中。
     */
    default boolean hasSkill() {
        return false;
    }

    /**
     * 更新技能流程状态。未迁移实体默认忽略写入。
     */
    default void setHasSkill(boolean hasSkill) {
    }

    /**
     * 返回实体是否已经进入只能执行死亡收尾指令的技能阶段。
     *
     * <p>默认返回 {@code false}。采用自定义死亡动画且不会立即进入
     * vanilla 死亡状态的实体应覆写此方法，让网络边界无需通过反射读取
     * 实体私有字段。
     */
    default boolean isSkillDeathSequenceActive() {
        return false;
    }

    /**
     * 返回实体是否正处于“等待斧头恢复”的调试状态。
     *
     * <p>仅供调试日志使用，默认返回 {@code false}。需要暴露该状态的实体
     * （如 {@code VindicatorGeneralEntity}）直接覆写本方法即可，网络边界
     * 不再通过反射读取实体私有字段。
     */
    default boolean isWaitingForAxeRecovery() {
        return false;
    }

    /**
     * 返回实体是否允许接收 {@code spawn} 出生收尾指令。
     *
     * <p>出生指令不是通用技能指令：只有实现了“服务端发起出生动画 -> 客户端关键帧
     * 回传完成信号”闭环的实体（当前为 {@code EliteLittlePersonGuardEntity}）才允许
     * 覆写本方法，并必须在覆写中同时编码“服务端已进入出生流程”这一可达合法状态
     * （例如服务端出生标记已置位、技能流程已启动、且完成信号尚未消费）。
     *
     * <p>默认返回 {@code false}，保证出生指令不会泛化到所有实体：未覆写的实体无论
     * 是否处于技能流程，策略层都会以 {@code SPAWN_NOT_ALLOWED} 拒绝。
     */
    default boolean canAcceptSpawnCommand() {
        return false;
    }

    /**
     * 执行一个已经通过服务端边界校验的动画关键帧指令。
     *
     * @param skillName 兼容现有网络协议的技能字符串
     * @return 指令是否被该实体识别并处理
     */
    default boolean handleSkillPayload(String skillName) {
        return false;
    }
}
