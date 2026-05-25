package com.kltyton.mob_battle.accessor;

public interface IStunAiState {
    boolean mobBattle$hasStunAiState();

    void mobBattle$beginStunAiState(boolean originalAiDisabled);

    boolean mobBattle$getOriginalAiDisabled();

    boolean mobBattle$hasExternalAiDisable();

    void mobBattle$setExternalAiDisable(boolean externalAiDisable);

    void mobBattle$clearStunAiState();
}
