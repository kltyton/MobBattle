package com.kltyton.mob_battle;

import com.kltyton.mob_battle.attributer.ModAttributer;
import com.kltyton.mob_battle.buff.ModBuffs;
import com.kltyton.mob_battle.command.ModCommands;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.event.ModEvents;
import com.kltyton.mob_battle.items.ModItemGroups;
import com.kltyton.mob_battle.items.ModItems;
import com.kltyton.mob_battle.network.ServerPlayNetwork;
import com.kltyton.mob_battle.sounds.ModSounds;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mob_battle implements ModInitializer {
    public static final String MOD_ID = "mob_battle";
    public static final Logger LOGGER = LoggerFactory.getLogger(Mob_battle.class);

    @Override
    public void onInitialize() {
        ModItems.init();
        ModBuffs.init();
        // 注册命令
        ModCommands.init();
        ModEvents.init();
        //注册实体
        ModEntities.init();
        ModAttributer.init();
        ServerPlayNetwork.init();
        ModItemGroups.init();
        ModSounds.init();
    }
}
