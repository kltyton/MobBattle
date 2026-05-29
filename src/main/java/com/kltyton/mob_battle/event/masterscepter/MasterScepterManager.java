package com.kltyton.mob_battle.event.masterscepter;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.items.scroll.PurificationScrollItem;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class MasterScepterManager {
    private static final Map<UUID, Map<String, Long>> COMMAND_COOLDOWNS = new ConcurrentHashMap<>();
    public static void runCommand(ServerPlayer player, String command) {
        Map<String, Long> playerCooldowns = COMMAND_COOLDOWNS.computeIfAbsent(player.getUUID(), k -> new ConcurrentHashMap<>());
        long now = player.level().getGameTime();

        // 定义每个命令的冷却时间（毫秒）
        long cooldownMs = switch (command) {
            case "d" -> 10 * 20;   // 10秒
            case "h" -> 7 * 20;        // 7秒
            case "pfwull" -> 7 * 20;
            case "sw" -> 10 * 20;
            case "j" -> 30 * 20;
            default -> 0;
        };
        Long lastUse = playerCooldowns.get(command);
        if (lastUse != null && now - lastUse < cooldownMs) {
            player.displayClientMessage(Component.literal("§c该命令正在冷却中！剩余 " + (cooldownMs - (now - lastUse)) / 20 + " 秒"), true);
            return;
        }

        boolean applied = switch (command) {
            case "fb" -> {
                UUID playerId = player.getUUID();

                if (!SbFb.ACTIVE_TASKS.contains(playerId)) {
                    SbFb.TASK_QUEUE.add(new SbFb.DelayedTask(
                            playerId,
                            player.level().dimension(),
                            50
                    ));
                    SbFb.ACTIVE_TASKS.add(playerId);
                }
                yield true;
            }
            case "bfb" -> {
                UUID playerId = player.getUUID();
                if (!SbBfb.ACTIVE_TASKS.contains(playerId)) {
                    SbBfb.TASK_QUEUE.add(new SbBfb.DelayedTask(
                            playerId,
                            player.level().dimension(),
                            30
                    ));
                    SbBfb.ACTIVE_TASKS.add(playerId);
                }
                yield true;
            }
            case "bfbp" -> {
                SbBfbp.runCommand(player, player.level());
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
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 4));
                yield true;
            }
            case "healttth" -> {
                // 生命恢复等级3
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 70 * 20, 2));
                yield true;
            }
            case "Resistanceee" -> {
                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 60 * 20, 1));
                yield true;
            }
            case "j" -> {
                PurificationScrollItem.removeStatusEffects(player,
                        MobEffects.MINING_FATIGUE,
                        MobEffects.BLINDNESS,
                        MobEffects.DARKNESS,
                        MobEffects.NAUSEA,
                        MobEffects.SLOWNESS,
                        ModEffects.STUN_ENTRY,
                        ModEffects.ICE_ENTRY
                );
                yield true;
            }
            default -> false;
        };

        if (applied) {
            playerCooldowns.put(command, now);
            player.displayClientMessage(Component.literal("§a效果已应用！"), true);
        } else {
            player.displayClientMessage(Component.literal("§c未知命令！"), true);
        }
    }

}
