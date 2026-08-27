package com.kltyton.mob_battle.items.armor.compressarmor;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.UUID;

/**
 * 压缩护甲静态玩家状态的生命周期管理。
 *
 * <p>解决压缩护甲技能类中按玩家 UUID 常驻静态 Map 的释放问题：玩家断线或
 * 服务器停止后，旧 UUID 状态不会自动消失，重新上线/下次开服时可能拿到过期状态。
 * 本类在断线时按 UUID 调用两类技能的 {@code clearPlayer}，在服务器停止时调用
 * {@code clearAll}，保证静态状态只覆盖在线会话。
 *
 * <p>生命周期约束：
 * <ul>
 *     <li>只注册 Fabric 服务端生命周期事件，所有回调都在服务端主线程执行，
 *         不创建任何线程；</li>
 *     <li>{@link #init()} 幂等：重复调用不会重复注册事件，避免热重载或
 *         多次初始化导致回调叠加；</li>
 *     <li>不依赖任何注册表或已注册内容，可安全地在运行时集成阶段任意位置接入。</li>
 * </ul>
 */
public final class CompressArmorPlayerStateLifecycle {

    /** 模组初始化在服务端主线程执行，普通布尔标记足以防止重复注册。 */
    private static boolean initialized;

    private CompressArmorPlayerStateLifecycle() {
    }

    /**
     * 注册断线与停服事件，释放压缩护甲技能类的静态玩家状态。
     * 幂等：首个调用执行注册，后续调用直接返回。
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID playerId = handler.getPlayer().getUUID();
            EcredcultistArmorSkills.clearPlayer(playerId);
            GoldArmorSkills.clearPlayer(playerId);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            EcredcultistArmorSkills.clearAll();
            GoldArmorSkills.clearAll();
        });
    }
}
