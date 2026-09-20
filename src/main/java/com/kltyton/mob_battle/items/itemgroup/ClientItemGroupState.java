package com.kltyton.mob_battle.items.itemgroup;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 客户端创造模式物品组开关的同步状态。
 */
@Environment(EnvType.CLIENT)
public final class ClientItemGroupState {
    private ClientItemGroupState() {
    }

    /** 服务端是否允许显示受控物品组。 */
    public static boolean isOpen = true;

    public static void reset() {
        isOpen = true;
    }
}
