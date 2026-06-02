package com.kltyton.mob_battle.config;

import com.kltyton.mob_battle.Mob_battle;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class MobBattleConfig {
    private static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue DEBUG_LOGGING;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        DEBUG_LOGGING = builder
                .comment("开启调试日志")
                .define("debugLogging", false);
        SPEC = builder.build();
    }

    public static void init() {
        ConfigRegistry.INSTANCE.register(Mob_battle.MOD_ID, ModConfig.Type.COMMON, SPEC);
    }

    public static boolean isDebugLoggingEnabled() {
        return DEBUG_LOGGING.get();
    }
}
