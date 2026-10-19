package com.kltyton.mob_battle.items.armor.presence;

/** 服务端同步的套装显示状态；客户端只能读取，伤害、冷却与召唤均由服务端裁决。 */
public interface PresenceArmorUser {
    int AMMO_MASK = 7;
    int AGGRESSIVE = 8;
    int STOMPING = 16;
    int EQUIPPED = 32;
    int COOLDOWN_SHIFT = 6;

    int mobBattle$getPresenceState();
    void mobBattle$setPresenceState(int state);
}
