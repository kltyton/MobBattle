package com.kltyton.mob_battle;

import com.kltyton.mob_battle.attributer.ModAttributer;
import com.kltyton.mob_battle.block.ModBlockEntities;
import com.kltyton.mob_battle.block.ModBlocks;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.command.ModCommands;
import com.kltyton.mob_battle.enchantment.ModEnchantments;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.ModEntityAttributes;
import com.kltyton.mob_battle.entity.drone.DroneManager;
import com.kltyton.mob_battle.entity.sensor.ModSensorTypes;
import com.kltyton.mob_battle.event.ClearItemEvent;
import com.kltyton.mob_battle.event.ModEvents;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.items.itemgroup.ModItemGroups;
import com.kltyton.mob_battle.network.ModPackets;
import com.kltyton.mob_battle.network.ServerPlayNetwork;
import com.kltyton.mob_battle.sounds.ModSounds;
import com.kltyton.mob_battle.sounds.bgm.ServerBgmManager;
import com.kltyton.mob_battle.utils.ModTrackedDataHandler;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mob_battle implements ModInitializer {
    public static final String MOD_ID = "mob_battle";
    public static final Logger LOGGER = LoggerFactory.getLogger(Mob_battle.class);

    @Override
    public void onInitialize() {
        ModTrackedDataHandler.init();
        ModSensorTypes.init();
        ModEntityAttributes.init();
        ModItems.init();
        ModEffects.init();
        ModEnchantments.init();
        ModCommands.init();
        ModEvents.init();
        ModEntities.init();
        ModBlocks.init();
        ModBlockEntities.init();
        ModAttributer.init();
        ModPackets.init();
        ServerPlayNetwork.init();
        ModSounds.init();
        ServerBgmManager.init();
        DroneManager.init();
        ModItemGroups.init();
        ClearItemEvent.init();
    }
}
