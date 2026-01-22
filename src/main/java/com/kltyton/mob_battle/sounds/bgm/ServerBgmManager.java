package com.kltyton.mob_battle.sounds.bgm;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.network.packet.SoundPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ServerBgmManager {

    public static void init() {
        // 自动加载与保存
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            List<BgmZone> list = BgmZoneStorage.load(server);
            list.forEach(z -> ZONES.put(z.name(), z));
            Mob_battle.LOGGER.info("[MobBattle] 已加载 BGM 区域: " + ZONES.size());
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            BgmZoneStorage.save(server, ZONES.values());
            Mob_battle.LOGGER.info("[MobBattle] 已保存 BGM 区域: " + ZONES.size());
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                BgmZone zone = getZoneFor(player);
                String currentZone = zone == null ? null : zone.name();
                String lastZone = LAST_ZONE.get(player.getUuid());

                if (!Objects.equals(currentZone, lastZone)) {
                    LAST_ZONE.put(player.getUuid(), currentZone);

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
    private static final Map<String, BgmZone> ZONES = new LinkedHashMap<>();
    private static final Map<UUID, String> LAST_ZONE = new HashMap<>();


    public static void addZone(BgmZone zone, MinecraftServer server) {
        ZONES.put(zone.name(), zone);
        BgmZoneStorage.save(server, ZONES.values());
    }

    public static boolean removeZone(String name, MinecraftServer server) {
        if (ZONES.remove(name) != null) {
            BgmZoneStorage.save(server, ZONES.values());
            return true;
        }
        return false;
    }

    @Nullable
    public static BgmZone getZoneFor(ServerPlayerEntity player) {
        Vec3d pos = player.getPos();
        for (BgmZone zone : ZONES.values()) {
            if (zone.contains(pos)) {
                return zone;
            }
        }
        return null;
    }

    @Nullable
    public static BgmZone getZone(String name) {
        return ZONES.get(name);
    }

    public static Collection<BgmZone> getAllZones() {
        return ZONES.values();
    }

    public static boolean mergeZones(String name1, String name2) {
        BgmZone a = ZONES.get(name1);
        BgmZone b = ZONES.get(name2);
        if (a == null || b == null) return false;

        Box boxA = a.area();
        Box boxB = b.area();
        // 扩展为包含两者的最小包围盒
        double minX = Math.min(boxA.minX, boxB.minX);
        double minY = Math.min(boxA.minY, boxB.minY);
        double minZ = Math.min(boxA.minZ, boxB.minZ);
        double maxX = Math.max(boxA.maxX, boxB.maxX);
        double maxY = Math.max(boxA.maxY, boxB.maxY);
        double maxZ = Math.max(boxA.maxZ, boxB.maxZ);

        BgmZone merged = new BgmZone(
                a.name(),
                new Box(minX, minY, minZ, maxX, maxY, maxZ),
                a.musicId(),
                a.volume()
        );
        ZONES.put(name1, merged);
        ZONES.remove(name2);
        return true;
    }

}

