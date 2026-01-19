package com.kltyton.mob_battle.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            TeamFightCommand.register(dispatcher);
            BgmCommand.register(dispatcher, registryAccess);
            AllianceCommand.register(dispatcher);
        });
        FriendlyDamageCommand.init();
    }
}
