package com.kltyton.mob_battle.event.masterscepter;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MasterScepterManager {
    private static final Map<UUID, Map<String, Long>> COMMAND_COOLDOWNS = new ConcurrentHashMap<>();
    public static void runCommand(ServerPlayerEntity player, String command) {
        Map<String, Long> playerCooldowns = COMMAND_COOLDOWNS.computeIfAbsent(player.getUuid(), k -> new ConcurrentHashMap<>());
        long now = player.getWorld().getTime();

        // 定义每个命令的冷却时间（毫秒）
        long cooldownMs = switch (command) {
            case "d" -> 10 * 20;   // 10秒
            case "h" -> 7 * 20;        // 7秒
            case "pfwull" -> 7 * 20;
            case "sw" -> 10 * 20;
            default -> 0;
        };
        Long lastUse = playerCooldowns.get(command);
        if (lastUse != null && now - lastUse < cooldownMs) {
            player.sendMessage(Text.literal("§c该命令正在冷却中！剩余 " + (cooldownMs - (now - lastUse)) / 20 + " 秒"), true);
            return;
        }

        boolean applied = switch (command) {
            case "fb" -> {
                UUID playerId = player.getUuid();

                if (!SbFb.ACTIVE_TASKS.contains(playerId)) {
                    SbFb.TASK_QUEUE.add(new SbFb.DelayedTask(
                            playerId,
                            player.getWorld().getRegistryKey(),
                            50
                    ));
                    SbFb.ACTIVE_TASKS.add(playerId);
                }
                yield true;
            }
            case "bfb" -> {
                UUID playerId = player.getUuid();
                if (!SbBfb.ACTIVE_TASKS.contains(playerId)) {
                    SbBfb.TASK_QUEUE.add(new SbBfb.DelayedTask(
                            playerId,
                            player.getWorld().getRegistryKey(),
                            30
                    ));
                    SbBfb.ACTIVE_TASKS.add(playerId);
                }
                yield true;
            }
            case "bfbp" -> {
                SbBfbp.runCommand(player, player.getWorld());
                yield true;
            }
            case "pfwull" -> {
                SbPfwull.runCommand(player);
                yield true;
            }
            case "d" -> {
                SbD.runCommand(player);
                yield true;
            }
            case "fw" -> {
                SbFw.runCommand(player);
                yield true;
            }
            case "s" -> {
                SbS.runCommand(player);
                yield true;
            }
            case "sw" -> {
                SbSw.runCommand(player);
                yield true;
            }
            case "h" -> {
                // 生命恢复等级5，持续3秒（60 tick）
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 60, 4));
                yield true;
            }
            case "healttth" -> {
                // 生命恢复等级3
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 70 * 20, 2));
                yield true;
            }
            case "Resistanceee" -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 60 * 20, 1));
                yield true;
            }
            default -> false;
        };

        if (applied) {
            playerCooldowns.put(command, now);
            player.sendMessage(Text.literal("§a效果已应用！"), true);
        } else {
            player.sendMessage(Text.literal("§c未知命令！"), true);
        }
    }

}
