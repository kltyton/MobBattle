package com.kltyton.mob_battle;

import com.kltyton.mob_battle.attributer.ModAttributer;
import com.kltyton.mob_battle.buff.ModBuffs;
import com.kltyton.mob_battle.command.ModCommands;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.drone.DroneManager;
import com.kltyton.mob_battle.entity.sensor.ModSensorTypes;
import com.kltyton.mob_battle.event.ModEvents;
import com.kltyton.mob_battle.items.ModItemGroups;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.network.ModPackets;
import com.kltyton.mob_battle.network.ServerPlayNetwork;
import com.kltyton.mob_battle.sounds.ModSounds;
import com.kltyton.mob_battle.sounds.bgm.ServerBgmManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mob_battle implements ModInitializer {
    public static final String MOD_ID = "mob_battle";
    public static final Logger LOGGER = LoggerFactory.getLogger(Mob_battle.class);

    @Override
    public void onInitialize() {
        ModSensorTypes.init();
        ModItems.init();
        ModBuffs.init();
        ModCommands.init();
        ModEvents.init();
        ModEntities.init();
        ModAttributer.init();
        ModPackets.init();
        ServerPlayNetwork.init();
        ModItemGroups.init();
        ModSounds.init();
        ServerBgmManager.init();
        DroneManager.init();
    }
}
