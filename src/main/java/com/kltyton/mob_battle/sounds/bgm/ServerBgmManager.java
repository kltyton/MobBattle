package com.kltyton.mob_battle.sounds.bgm;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.network.packet.SoundPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ServerBgmManager {

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTING.register(ServerBgmManager::clearServerState);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerState state = new ServerState();
            List<BgmZone> list = BgmZoneStorage.load(server);
            list.forEach(z -> state.zones.put(z.name(), z));
            STATES.put(server, state);
            Mob_battle.LOGGER.info("[MobBattle] 已加载 BGM 区域: " + state.zones.size());
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            ServerState state = STATES.get(server);
            if (state != null) {
                BgmZoneStorage.save(server, state.zones.values());
                Mob_battle.LOGGER.info("[MobBattle] 已保存 BGM 区域: " + state.zones.size());
            }
            clearServerState(server);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(ServerBgmManager::clearServerState);

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerState state = STATES.get(server);
            if (state != null) {
                state.clearPlayer(handler.getPlayer().getUUID());
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ServerState state = STATES.get(server);
            if (state == null) {
                return;
            }
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                BgmZone zone = getZoneFor(player, state);
                String currentZone = zone == null ? null : zone.name();

                if (shouldPlay(state, player.getUUID(), currentZone)) {
                    if (zone == null) {
                        // 离开区域 -> 发送淡出指令而不是立即停止
                        // 这里发送一个特殊的指令告诉客户端开始淡出
                        ServerPlayNetworking.send(player, new SoundPayload("fade_out", 0f));
                    } else {
                        // 进入新区域 -> 发送正常播放指令
                        ServerPlayNetworking.send(player, new SoundPayload(zone.musicId().toString(), zone.volume()));
                    }
                }
            }
        });
    }

    private static final Map<MinecraftServer, ServerState> STATES = new IdentityHashMap<>();


    public static void addZone(BgmZone zone, MinecraftServer server) {
        ServerState state = stateFor(server);
        state.zones.put(zone.name(), zone);
        BgmZoneStorage.save(server, state.zones.values());
    }

    public static boolean removeZone(String name, MinecraftServer server) {
        ServerState state = stateFor(server);
        if (state.zones.remove(name) != null) {
            BgmZoneStorage.save(server, state.zones.values());
            return true;
        }
        return false;
    }

    @Nullable
    public static BgmZone getZoneFor(ServerPlayer player) {
        ServerState state = STATES.get(player.level().getServer());
        return state == null ? null : getZoneFor(player, state);
    }

    private static BgmZone getZoneFor(ServerPlayer player, ServerState state) {
        Vec3 pos = player.position();
        for (BgmZone zone : state.zones.values()) {
            if (zone.contains(pos)) {
                return zone;
            }
        }
        return null;
    }

    @Nullable
    public static BgmZone getZone(MinecraftServer server, String name) {
        ServerState state = STATES.get(server);
        return state == null ? null : state.zones.get(name);
    }

    public static Collection<BgmZone> getAllZones(MinecraftServer server) {
        ServerState state = STATES.get(server);
        return state == null ? java.util.Collections.emptyList() : state.zones.values();
    }

    public static boolean mergeZones(MinecraftServer server, String name1, String name2) {
        ServerState state = STATES.get(server);
        if (state == null) return false;

        BgmZone a = state.zones.get(name1);
        BgmZone b = state.zones.get(name2);
        if (a == null || b == null) return false;

        AABB boxA = a.area();
        AABB boxB = b.area();
        // 扩展为包含两者的最小包围盒
        double minX = Math.min(boxA.minX, boxB.minX);
        double minY = Math.min(boxA.minY, boxB.minY);
        double minZ = Math.min(boxA.minZ, boxB.minZ);
        double maxX = Math.max(boxA.maxX, boxB.maxX);
        double maxY = Math.max(boxA.maxY, boxB.maxY);
        double maxZ = Math.max(boxA.maxZ, boxB.maxZ);

        BgmZone merged = new BgmZone(
                a.name(),
                new AABB(minX, minY, minZ, maxX, maxY, maxZ),
                a.musicId(),
                a.volume()
        );
        state.zones.put(name1, merged);
        state.zones.remove(name2);
        return true;
    }

    static boolean shouldPlay(ServerState state, UUID playerId, @Nullable String currentZone) {
        String lastZone = state.lastZones.put(playerId, currentZone);
        return !Objects.equals(currentZone, lastZone);
    }

    private static ServerState stateFor(MinecraftServer server) {
        return STATES.computeIfAbsent(server, ignored -> new ServerState());
    }

    private static void clearServerState(MinecraftServer server) {
        ServerState state = STATES.remove(server);
        if (state != null) {
            state.clearAll();
        }
    }

    static final class ServerState {
        private final Map<String, BgmZone> zones = new LinkedHashMap<>();
        private final Map<UUID, String> lastZones = new HashMap<>();

        void clearPlayer(UUID playerId) {
            lastZones.remove(playerId);
        }

        void clearAll() {
            zones.clear();
            lastZones.clear();
        }
    }

}

