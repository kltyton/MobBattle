package com.kltyton.mob_battle.event.masterscepter;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.items.scroll.PurificationScrollItem;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class MasterScepterManager {
    private static final Map<MinecraftServer, Map<UUID, Map<String, Long>>> COMMAND_COOLDOWNS = new IdentityHashMap<>();
    private static final AtomicBoolean LIFECYCLE_REGISTERED = new AtomicBoolean();

    /** 注册权杖冷却的断线与停服清理，避免跨 server/JVM 生命周期复用。 */
    public static void initLifecycle() {
        if (!LIFECYCLE_REGISTERED.compareAndSet(false, true)) {
            return;
        }
        SbFb.initLifecycle();
        SbBfb.initLifecycle();
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                clearPlayer(server, handler.getPlayer().getUUID()));
        ServerLifecycleEvents.SERVER_STOPPING.register(MasterScepterManager::clearServer);
        ServerLifecycleEvents.SERVER_STOPPED.register(MasterScepterManager::clearServer);
    }

    public static void runCommand(ServerPlayer player, String command) {
        MinecraftServer server = player.level().getServer();
        Map<UUID, Map<String, Long>> serverCooldowns = COMMAND_COOLDOWNS.computeIfAbsent(
                server, ignored -> new HashMap<>());
        Map<String, Long> playerCooldowns = serverCooldowns.computeIfAbsent(player.getUUID(),
                ignored -> new HashMap<>());
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
            player.sendOverlayMessage(Component.literal("§c该命令正在冷却中！剩余 " + (cooldownMs - (now - lastUse)) / 20 + " 秒"));
            return;
        }

        boolean applied = switch (command) {
            case "fb" -> {
                UUID playerId = player.getUUID();
                SbFb.tryStart(server, playerId, player.level().dimension());
                yield true;
            }
            case "bfb" -> {
                UUID playerId = player.getUUID();
                SbBfb.tryStart(server, playerId, player.level().dimension());
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
            player.sendOverlayMessage(Component.literal("§a效果已应用！"));
        } else {
            player.sendOverlayMessage(Component.literal("§c未知命令！"));
        }
    }

    /** 断线清除单个玩家的冷却；由 Fabric 连接生命周期回调调用。 */
    static void clearPlayer(MinecraftServer server, UUID playerId) {
        SbFb.clearPlayer(server, playerId);
        SbBfb.clearPlayer(server, playerId);
        Map<UUID, Map<String, Long>> serverCooldowns = COMMAND_COOLDOWNS.get(server);
        if (serverCooldowns == null) {
            return;
        }
        serverCooldowns.remove(playerId);
        if (serverCooldowns.isEmpty()) {
            COMMAND_COOLDOWNS.remove(server);
        }
    }

    /** 停服清除该 server 的全部冷却，避免旧世界状态进入下一次启动。 */
    static void clearServer(MinecraftServer server) {
        SbFb.clearServer(server);
        SbBfb.clearServer(server);
        COMMAND_COOLDOWNS.remove(server);
    }

}
