package com.kltyton.mob_battle.network;

import com.kltyton.mob_battle.network.receiver.client.ClientAnimationReceivers;
import com.kltyton.mob_battle.network.receiver.client.ClientBossBarReceiver;
import com.kltyton.mob_battle.network.receiver.client.ClientEffectBridgeReceiver;
import com.kltyton.mob_battle.network.receiver.client.ClientEntityStateReceivers;
import com.kltyton.mob_battle.network.receiver.client.ClientPerspectiveSyncReceiver;
import com.kltyton.mob_battle.network.receiver.client.ClientSoundReceiver;
import com.kltyton.mob_battle.network.receiver.client.ClientStateSyncReceivers;

/**
 * 客户端网络接收器兼容性聚合入口。
 *
 * <p>仅负责按固定顺序注册全部 9 个客户端接收器，具体处理逻辑已按职责拆分到
 * {@code receiver.client} 包下的独立接收器类中。注册顺序必须与原实现保持一致，
 * 不得随意调整，以免破坏既有的载荷注册顺序。</p>
 */
public final class ClientPlayNetwork {
    private ClientPlayNetwork() {
    }

    /**
     * 按原注册顺序聚合注册全部 9 个客户端接收器。
     */
    public static void init() {
        ClientSoundReceiver.registerSound();
        ClientStateSyncReceivers.registerItemGroup();
        ClientEntityStateReceivers.registerILeadUpdate();
        ClientPerspectiveSyncReceiver.registerPlayerSkillUtil();
        ClientStateSyncReceivers.registerPermission();
        ClientBossBarReceiver.registerCustomBossBar();
        ClientAnimationReceivers.registerPlayerAnimation();
        ClientAnimationReceivers.registerPalMorePlayerAnimation();
        ClientEffectBridgeReceiver.registerParticleStormEmitter();
    }
}
