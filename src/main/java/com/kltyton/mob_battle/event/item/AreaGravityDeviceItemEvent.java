package com.kltyton.mob_battle.event.item;

import com.kltyton.mob_battle.items.gravity.AreaGravityFields;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class AreaGravityDeviceItemEvent {
    public static void init() {
        ServerTickEvents.END_LEVEL_TICK.register(AreaGravityFields::tickWorld);

        // 世界卸载时清掉该世界的场状态，防止静态表跨存档持有 ServerLevel 强引用。
        ServerLevelEvents.UNLOAD.register((server, world) -> AreaGravityFields.removeWorld(world));

        // 服务端停止时兜底清空全部场，与 UNLOAD 一起覆盖正常卸载与异常退出路径。
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> AreaGravityFields.clearAllFields());
    }
}
