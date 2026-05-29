package com.kltyton.mob_battle.command;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;

public class FriendlyDamageCommand {
    public static GameRules.Key<GameRules.BooleanValue> ENABLE_FRIENDLY_PROJECTILE_DAMAGE;
    public static GameRules.Key<GameRules.BooleanValue> ENABLE_FRIENDLY_DAMAGE;

    public static void init() {
        ENABLE_FRIENDLY_PROJECTILE_DAMAGE = GameRuleRegistry.register(
                "enableFriendlyProjectileDamage",
                GameRules.Category.PLAYER,
                GameRuleFactory.createBooleanRule(true)
        );
        ENABLE_FRIENDLY_DAMAGE = GameRuleRegistry.register(
                "enableFriendlyDamage",
                GameRules.Category.PLAYER,
                GameRuleFactory.createBooleanRule(true)
        );
    }
}
