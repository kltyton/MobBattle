package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.event.team.TeamFightHandler;

public class ModEvents {
    public static void init() {
        EntitySelectionEvent.init();
        TeamFightHandler.init();
        BuffStunEvent.init();
        PlayerAttackEvent.init();
    }
}
