package com.kltyton.mob_battle.client;

import com.kltyton.mob_battle.Mob_battle;

public final class ModPlayerAnimationControllers {
    private ModPlayerAnimationControllers() {
    }

    public static void init() {
        try {
            PalMoreClientBridge.initKnifeController();
        } catch (LinkageError error) {
            Mob_battle.LOGGER.warn("初始化 PALMR 刀类动画桥接失败。", error);
        }
    }
}
