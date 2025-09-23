package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.event.team.TeamFightHandler;

public class ModEvents {
    public static void init() {
        EntitySelectionHandlerYH.register();
        TeamFightHandler.register();
        TaskScheduler.register();
    }
}
