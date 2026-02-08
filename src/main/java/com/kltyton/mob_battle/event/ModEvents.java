package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.event.effect.EffectEventHandler;
import com.kltyton.mob_battle.event.flowerfairy.FlowerFairyEntityEvent;
import com.kltyton.mob_battle.event.team.TeamFightHandler;

public class ModEvents {
    public static void init() {
        ModMainEvents.init();
        EffectEventHandler.init();
        EntitySelectionEvent.init();
        TeamFightHandler.init();
        BuffStunEvent.init();
        PlayerAttackEvent.init();
        SelfDestructEffectEvent.init();
        FlowerFairyEntityEvent.init();
    }
}
