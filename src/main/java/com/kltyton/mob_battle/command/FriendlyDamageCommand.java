package com.kltyton.mob_battle.command;

import com.kltyton.mob_battle.Mob_battle;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;

public class FriendlyDamageCommand {
    public static GameRule<Boolean> ENABLE_FRIENDLY_PROJECTILE_DAMAGE;
    public static GameRule<Boolean> ENABLE_FRIENDLY_DAMAGE;

    public static void init() {
        ENABLE_FRIENDLY_PROJECTILE_DAMAGE = registerBoolean("enable_friendly_projectile_damage", true);
        ENABLE_FRIENDLY_DAMAGE = registerBoolean("enable_friendly_damage", true);
    }

    private static GameRule<Boolean> registerBoolean(String name, boolean defaultValue) {
        return Registry.register(
                BuiltInRegistries.GAME_RULE,
                Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, name),
                new GameRule<>(
                        GameRuleCategory.PLAYER,
                        GameRuleType.BOOL,
                        BoolArgumentType.bool(),
                        GameRuleTypeVisitor::visitBoolean,
                        Codec.BOOL,
                        value -> value ? 1 : 0,
                        defaultValue,
                        FeatureFlags.VANILLA_SET
                )
        );
    }
}
