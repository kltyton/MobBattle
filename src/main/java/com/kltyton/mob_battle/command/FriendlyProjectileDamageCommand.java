package com.kltyton.mob_battle.command;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class FriendlyProjectileDamageCommand {
    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_FRIENDLY_PROJECTILE_DAMAGE =
            GameRuleRegistry.register(
                    "enableFriendlyProjectileDamage",
                    GameRules.Category.PLAYER,
                    GameRuleFactory.createBooleanRule(true) // 默认关闭队友伤害保护
            );

    public static void register() {

    }
}
