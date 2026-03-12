package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.event.effect.EffectEventHandler;
import com.kltyton.mob_battle.event.flowerfairy.FlowerFairyEntityEvent;
import com.kltyton.mob_battle.event.item.GuardianSealItemEvent;
import com.kltyton.mob_battle.event.player.DeathPenaltyEvents;
import com.kltyton.mob_battle.event.team.TeamFightHandler;

public class ModEvents {
    public static void init() {
        ModServerEvents.init();
        EffectEventHandler.init();
        EntitySelectionEvent.init();
        TeamFightHandler.init();
        BuffStunEvent.init();
        PlayerAttackEvent.init();
        SelfDestructEffectEvent.init();
        FlowerFairyEntityEvent.init();
        GuardianSealItemEvent.init();
        DeathPenaltyEvents.init();
    }
}
