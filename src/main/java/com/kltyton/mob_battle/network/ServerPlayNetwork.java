package com.kltyton.mob_battle.network;

import com.kltyton.mob_battle.network.receiver.server.ArmorSkillReceivers;
import com.kltyton.mob_battle.network.receiver.server.EntityFrameSyncReceivers;
import com.kltyton.mob_battle.network.receiver.server.GeneralRequestReceivers;
import com.kltyton.mob_battle.network.receiver.server.HeldItemActionReceivers;
import com.kltyton.mob_battle.network.receiver.server.PlayerSkillReceiver;
import com.kltyton.mob_battle.network.receiver.server.SkillPayloadReceiver;

/**
 * 服务端 Play 网络接收器的稳定装配入口。
 *
 * <p>本类只负责保持历史注册顺序，不再承载具体业务逻辑。每个接收器按领域
 * 位于 {@code network.receiver.server}，并在自身边界内验证客户端输入。
 * 注册顺序属于兼容契约；新增 Payload 时应把它放入对应领域接收器，并在此处
 * 明确插入顺序，同时更新网络兼容账本与文档。
 */
public final class ServerPlayNetwork {
    private ServerPlayNetwork() {
    }

    /**
     * 按重构前完全相同的顺序注册十四个服务端接收器。
     */
    public static void init() {
        // 1~3：实体动画帧同步。
        EntityFrameSyncReceivers.init();

        // 4~6：实体技能、玩家技能与手持物品输入。
        SkillPayloadReceiver.init();
        PlayerSkillReceiver.init();
        HeldItemActionReceivers.initLeftClick();

        // 7~10：附魔、无人机、创造标签页权限与主权杖。
        GeneralRequestReceivers.initEnchantment();
        GeneralRequestReceivers.initSummonDrone();
        GeneralRequestReceivers.initItemGroup();
        GeneralRequestReceivers.initMasterScepter();

        // 11~14：护甲技能；第 13 位仍是手持武器形态切换。
        ArmorSkillReceivers.initShieldSpawn();
        ArmorSkillReceivers.initZiJin();
        HeldItemActionReceivers.initPiglinCannonMode();
        ArmorSkillReceivers.initCompressArmorSkill();
    }
}
