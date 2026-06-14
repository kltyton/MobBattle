package com.kltyton.mob_battle.command;

import com.kltyton.mob_battle.Mob_battle;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;

public final class CombatLogSystem {
    public static GameRule<Boolean> COMBAT_LOG_SYSTEM;

    private CombatLogSystem() {
    }

    public static void init() {
        COMBAT_LOG_SYSTEM = Registry.register(
                BuiltInRegistries.GAME_RULE,
                Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "combat_log_system"),
                new GameRule<>(
                        GameRuleCategory.MOBS,
                        GameRuleType.BOOL,
                        BoolArgumentType.bool(),
                        GameRuleTypeVisitor::visitBoolean,
                        Codec.BOOL,
                        value -> value ? 1 : 0,
                        false,
                        FeatureFlags.VANILLA_SET
                )
        );
    }

    public static boolean isEnabled(ServerLevel level) {
        return COMBAT_LOG_SYSTEM != null && level.getGameRules().get(COMBAT_LOG_SYSTEM);
    }

    public static void logDamage(LivingEntity target, DamageSource source, float requestedAmount,
                                 float healthBefore, float healthAfter, boolean accepted) {
        if (!(target.level() instanceof ServerLevel level) || !isEnabled(level)) {
            return;
        }

        Entity attacker = source.getEntity();
        Component actor = attacker == null
                ? colored(source.getMsgId(), ChatFormatting.DARK_RED)
                : describe(attacker);
        float actualDamage = Math.max(0.0F, healthBefore - healthAfter);
        MutableComponent action = Component.empty().append(actor).append(" 对 ").append(describe(target));
        if (accepted) {
            action.append(" 造成 ")
                    .append(number(actualDamage, ChatFormatting.RED))
                    .append(colored(" 点伤害", ChatFormatting.RED))
                    .append("（请求 ")
                    .append(number(requestedAmount, ChatFormatting.YELLOW))
                    .append("，生命 ")
                    .append(number(healthBefore, ChatFormatting.YELLOW))
                    .append(" -> ")
                    .append(number(healthAfter, ChatFormatting.YELLOW))
                    .append("）");
        } else {
            action.append(" 的 ")
                    .append(number(requestedAmount, ChatFormatting.RED))
                    .append(colored(" 点伤害被阻止", ChatFormatting.GRAY));
        }
        broadcast(level, action);
    }

    public static void logHeal(LivingEntity entity, float requestedAmount, float healthBefore, float healthAfter) {
        if (!(entity.level() instanceof ServerLevel level) || !isEnabled(level) || healthAfter <= healthBefore) {
            return;
        }

        broadcast(level, Component.empty()
                .append(describe(entity))
                .append(colored(" 治疗了 ", ChatFormatting.GREEN))
                .append(number(healthAfter - healthBefore, ChatFormatting.GREEN))
                .append(colored(" 点生命", ChatFormatting.GREEN))
                .append("（请求 ")
                .append(number(requestedAmount, ChatFormatting.YELLOW))
                .append("，生命 ")
                .append(number(healthBefore, ChatFormatting.YELLOW))
                .append(" -> ")
                .append(number(healthAfter, ChatFormatting.GREEN))
                .append("）"));
    }

    public static void logDeath(LivingEntity entity, DamageSource source) {
        if (!(entity.level() instanceof ServerLevel level) || !isEnabled(level)) {
            return;
        }

        Entity attacker = source.getEntity();
        Component actor = attacker == null
                ? colored(source.getMsgId(), ChatFormatting.DARK_RED)
                : describe(attacker);
        broadcast(level, Component.empty()
                .append(describe(entity))
                .append(colored(" 被 ", ChatFormatting.GRAY))
                .append(actor)
                .append(colored(" 击杀", ChatFormatting.DARK_RED)));
    }

    public static void logEffectAdded(LivingEntity entity, MobEffectInstance effect, Entity source) {
        if (!(entity.level() instanceof ServerLevel level) || !isEnabled(level)) {
            return;
        }

        Component sourceText = source == null
                ? colored("环境/自身", ChatFormatting.GRAY)
                : describe(source);
        broadcast(level, Component.empty()
                .append(sourceText)
                .append(" 对 ")
                .append(describe(entity))
                .append(colored(" 施加效果 ", ChatFormatting.BLUE))
                .append(effect.getEffect().value().getDisplayName().copy().withStyle(ChatFormatting.BLUE))
                .append(" ")
                .append(colored(Integer.toString(effect.getAmplifier() + 1), ChatFormatting.AQUA))
                .append("（")
                .append(colored(Integer.toString(effect.getDuration()), ChatFormatting.YELLOW))
                .append(" tick）"));
    }

    public static void logEffectRemoved(LivingEntity entity, String effectName) {
        if (entity.level() instanceof ServerLevel level && isEnabled(level)) {
            broadcast(level, Component.empty()
                    .append(describe(entity))
                    .append(" 的效果 ")
                    .append(colored(effectName, ChatFormatting.BLUE))
                    .append(colored(" 已移除", ChatFormatting.GRAY)));
        }
    }

    public static void logSkill(Entity entity, String skill) {
        if (entity.level() instanceof ServerLevel level && isEnabled(level)) {
            broadcast(level, Component.empty()
                    .append(describe(entity))
                    .append(colored(" 使用技能/行为：", ChatFormatting.LIGHT_PURPLE))
                    .append(colored(skill, ChatFormatting.LIGHT_PURPLE)));
        }
    }

    public static void logAction(Entity entity, String action) {
        if (entity.level() instanceof ServerLevel level && isEnabled(level)) {
            broadcast(level, Component.empty()
                    .append(describe(entity))
                    .append("：")
                    .append(colored(action, ChatFormatting.GOLD)));
        }
    }

    private static void broadcast(ServerLevel level, Component message) {
        level.getServer().getPlayerList().broadcastSystemMessage(
                Component.literal("[战斗日志] ").withStyle(ChatFormatting.GOLD).append(message),
                false
        );
    }

    private static Component describe(Entity entity) {
        Identifier typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return entity.getDisplayName().copy()
                .withStyle(ChatFormatting.AQUA)
                .append(colored("[" + typeId + "#" + entity.getId() + "]", ChatFormatting.DARK_AQUA));
    }

    private static Component number(float value, ChatFormatting color) {
        return colored(String.format(java.util.Locale.ROOT, "%.1f", value), color);
    }

    private static Component colored(String text, ChatFormatting color) {
        return Component.literal(text).withStyle(color);
    }
}
