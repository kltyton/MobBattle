package com.kltyton.mob_battle.accessor;

public interface IPiglinBruteSpearMode {
    String SPEAR_ATTACK_MODE_KEY = "MobBattleSpearAttackMode";
    String FORCE_GOLDEN_SPEAR_KEY = "MobBattleForceGoldenSpear";
    int SPEAR_MODE_NONE = 0;
    int SPEAR_MODE_USE = 1;

    int mobBattle$getSpearAttackMode();

    void mobBattle$setSpearAttackMode(int mode);

    default boolean mobBattle$usesSpearAsItem() {
        return mobBattle$getSpearAttackMode() == SPEAR_MODE_USE;
    }
}
