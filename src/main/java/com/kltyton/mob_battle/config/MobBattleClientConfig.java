package com.kltyton.mob_battle.config;

import com.kltyton.mob_battle.Mob_battle;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class MobBattleClientConfig {
    private static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue SHOW_FIRST_PERSON;
    private static final ModConfigSpec.BooleanValue SHOW_CHEST_ARMOR;
    public static final ModConfigSpec.BooleanValue SHOW_RIGHT_ARMOR;
    public static final ModConfigSpec.BooleanValue SHOW_LEFT_ARMOR;
    public static final ModConfigSpec.BooleanValue SHOW_RIGHT_HAND_ITEM;
    public static final ModConfigSpec.BooleanValue SHOW_LEFT_HAND_ITEM;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        SHOW_FIRST_PERSON = builder
                .comment("在第一人称显示玩家动画")
                .define("showFirstPerson", true);
        SHOW_CHEST_ARMOR = builder
                .comment("在第一人称显示玩家动画的时候显示玩家胸甲")
                .define("showChestArmor", true);
        SHOW_RIGHT_ARMOR = builder
                .comment("在第一人称显示玩家动画的时候显示玩家右手甲")
                .define("showRightArmor", true);
        SHOW_LEFT_ARMOR = builder
                .comment("在第一人称显示玩家动画的时候显示玩家左手甲")
                .define("showLeftArmor", true);
        SHOW_RIGHT_HAND_ITEM = builder
                .comment("在第一人称显示玩家动画的时候显示玩家右手物品")
                .define("showRightHandItem", true);
        SHOW_LEFT_HAND_ITEM = builder
                .comment("在第一人称显示玩家动画的时候显示玩家左手物品")
                .define("showLeftHandItem", true);
        SPEC = builder.build();
    }

    public static void init() {
        ConfigRegistry.INSTANCE.register(Mob_battle.MOD_ID, ModConfig.Type.CLIENT, SPEC);
    }

    public static boolean showFirstPerson() {
        return SHOW_FIRST_PERSON.get();
    }
    public static boolean showChestArmor() {
        return SHOW_CHEST_ARMOR.get();
    }
    public static boolean showRightArmor() {
        return SHOW_RIGHT_ARMOR.get();
    }
    public static boolean showLeftArmor() {
        return SHOW_LEFT_ARMOR.get();
    }
    public static boolean showRightHandItem() {
        return SHOW_RIGHT_HAND_ITEM.get();
    }
    public static boolean showLeftHandItem() {
        return SHOW_LEFT_HAND_ITEM.get();
    }
}
