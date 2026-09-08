package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.event.boss.dragon.EndPlayerDeathDragonHealingEvent;
import com.kltyton.mob_battle.event.effect.EffectEventHandler;
import com.kltyton.mob_battle.event.flowerfairy.FlowerFairyEntityEvent;
import com.kltyton.mob_battle.event.golem.ChestGolemBuildEvent;
import com.kltyton.mob_battle.event.item.AreaGravityDeviceItemEvent;
import com.kltyton.mob_battle.event.item.GuardianSealItemEvent;
import com.kltyton.mob_battle.event.littleperson.Xbot002SummonEvent;
import com.kltyton.mob_battle.event.player.DeathPenaltyEvents;
import com.kltyton.mob_battle.event.player.PermissionEvents;
import com.kltyton.mob_battle.event.team.TeamFightHandler;
import com.kltyton.mob_battle.items.tool.moneygun.MoneyGunItem;
import com.kltyton.mob_battle.block.berryjuice.BerryJuiceCauldronInteractions;
import com.kltyton.mob_battle.event.world.witchhut.WitchHutLifecycle;
import com.kltyton.mob_battle.items.scroll.FiremanScrollItem;

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
        ChestGolemBuildEvent.init();
        Xbot002SummonEvent.init();
        com.kltyton.mob_battle.event.littleperson.zombie.ZombieLittlePersonLifecycle.init();
        GuardianSealItemEvent.init();
        AreaGravityDeviceItemEvent.init();
        DeathPenaltyEvents.init();
        EndPlayerDeathDragonHealingEvent.init();
        PermissionEvents.init();
        MoneyGunItem.initLifecycle();
        BerryJuiceCauldronInteractions.init();
        WitchHutLifecycle.init();
        FiremanScrollItem.initLifecycle();
    }
}
