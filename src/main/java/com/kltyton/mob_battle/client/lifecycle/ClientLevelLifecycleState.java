package com.kltyton.mob_battle.client.lifecycle;

/**
 * 维护客户端世界生命周期观察状态，并把候选集合清理交给调用方。
 *
 * <p>状态按对象身份区分世界：首次观察非空世界只建立基线，切换到不同世界或断开时
 * 执行一次清理。该类不依赖 Minecraft 类型，便于直接覆盖生命周期边界。
 */
public final class ClientLevelLifecycleState<T> {
    private T lastLevel;

    /**
     * 观察当前客户端世界。
     *
     * @param currentLevel 当前世界；{@code null} 表示断开
     * @param clearCandidates 清理候选集合的动作
     */
    public void observe(T currentLevel, Runnable clearCandidates) {
        if (currentLevel == null) {
            lastLevel = null;
            clearCandidates.run();
            return;
        }
        if (lastLevel == null) {
            lastLevel = currentLevel;
            return;
        }
        if (lastLevel != currentLevel) {
            lastLevel = currentLevel;
            clearCandidates.run();
        }
    }
}
