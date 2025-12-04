package com.kltyton.mob_battle.entity.drone;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.drone.attackdrone.AttackDroneEntity;
import com.kltyton.mob_battle.entity.drone.treatmentdrone.TreatmentDroneEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DroneManager {
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var data : DroneManager.INSTANCE.playerDrones.values()) {
                if (data.cooldownTicks > 0) data.cooldownTicks--;
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            DroneManager.INSTANCE.removePlayer(handler.getPlayer().getUuid());
        });
    }
    public static void handleSummonRequest(ServerPlayerEntity player) {
        DroneData data = DroneManager.INSTANCE.getOrCreate(player);

        // 正在冷却中 → 收回无人机
        if (data.cooldownTicks > 0) {
            int aliveBefore = 0;
            if (getEntity(player, data.attack1) != null) aliveBefore++;
            if (getEntity(player, data.attack2) != null) aliveBefore++;
            if (getEntity(player, data.treatment) != null) aliveBefore++;

            clearAllDrones(player, data);

            int missing = 3 - aliveBefore;
            if (missing == 0) {
                sendFeedback(player, Text.literal("无人机已全部回收！")
                        .formatted(Formatting.GREEN));
            } else if (missing < 3) {
                data.cooldownTicks += 700 * missing;
                sendFeedback(player, Text.literal("无人机强制回收！")
                        .formatted(Formatting.RED)
                        .append(Text.literal(" 缺失 " + missing + " 个，额外惩罚冷却 " + (35 * missing) + " 秒")
                                .formatted(Formatting.GRAY)));
            } else sendFeedback(player, Text.literal("没有无人机可以回收，")
                    .formatted(Formatting.RED)
                    .append(Text.literal("剩下冷却时间 " + data.cooldownTicks / 20 + " 秒")
                            .formatted(Formatting.GRAY)));
            return;
        }

        // 统计缺失数量
        int missing = 0;
        if (getEntity(player, data.attack1) == null) missing++;
        if (getEntity(player, data.attack2) == null) missing++;
        if (getEntity(player, data.treatment) == null) missing++;

        // 全召唤
        if (missing == 3) {
            spawnAllThree(player, data);
            data.cooldownTicks = 1200;
            sendFeedback(player, Text.literal("召唤无人机小队成功！")
                    .formatted(Formatting.AQUA)
                    .append(Text.literal(" 冷却 60 秒").formatted(Formatting.GRAY)));
        }
        // 补全
        else if (missing > 0) {
            spawnMissing(player, data);
            data.cooldownTicks = 700 * missing + 1200;
            sendFeedback(player, Text.literal("补全无人机 ×" + missing)
                    .formatted(Formatting.YELLOW)
                    .append(Text.literal(" 冷却 " + (35 * missing) + " 秒").formatted(Formatting.GRAY)));
        } else {
            sendFeedback(player, Text.literal("无人机小队已存在且完整！不需要补全或召唤！").formatted(Formatting.GREEN));
        }
    }
    public static void handleAttackDroneMode(ServerPlayerEntity player) {
        DroneData data = DroneManager.INSTANCE.getOrCreate(player);
        Entity attack1 = getEntity(player, data.attack1);
        Entity attack2 = getEntity(player, data.attack2);
        if (attack1 instanceof AttackDroneEntity attackDrone1) {
            attackDrone1.cycleCombatMode();
            sendFeedback(player, Text.literal("切换攻击模式：" + attackDrone1.getCombatMode().getDisplayName()));
        }
        if (attack2 instanceof AttackDroneEntity attackDrone2) {
            attackDrone2.cycleCombatMode();
            sendFeedback(player, Text.literal("切换攻击模式：" + attackDrone2.getCombatMode().getDisplayName()));
        }
    }
    public static void handleTreatmentDroneMode(ServerPlayerEntity player) {
        DroneData data = DroneManager.INSTANCE.getOrCreate(player);
        Entity treatment = getEntity(player, data.treatment);
        if (treatment instanceof TreatmentDroneEntity treatmentDrone) {
            treatmentDrone.setOnlyPlayer(!treatmentDrone.isOnlyPlayer());
            sendFeedback(player, Text.literal("切换治疗模式：" + (treatmentDrone.isOnlyPlayer() ? "§e玩家" : "§c非玩家")));
        }
    }
    private static void spawnAllThree(ServerPlayerEntity player, DroneManager.DroneData data) {
        Vec3d pos = player.getPos();
        var world = player.getWorld();

        AttackDroneEntity a1 = new AttackDroneEntity(ModEntities.ATTACK_DRONE, world);
        AttackDroneEntity a2 = new AttackDroneEntity(ModEntities.ATTACK_DRONE, world);
        TreatmentDroneEntity t = new TreatmentDroneEntity(ModEntities.TREATMENT_DRONE, world);

        a1.setPosition(pos.add(2, 1, 0));
        a2.setPosition(pos.add(-2, 1, 0));
        t.setPosition(pos.add(0, 1, 2));

        world.spawnEntity(a1);
        world.spawnEntity(a2);
        world.spawnEntity(t);

        initDrones(player, a1);
        initDrones(player, a2);
        initDrones(player, t);

        // 恢复血量
        data.applyRecordedHealthAndClear(a1, "attack1");
        data.applyRecordedHealthAndClear(a2, "attack2");
        data.applyRecordedHealthAndClear(t,  "treatment");

        data.attack1 = a1.getUuid();
        data.attack2 = a2.getUuid();
        data.treatment = t.getUuid();
    }
    public static void initDrones(ServerPlayerEntity player, DroneEntity entity) {
        entity.setTamedBy(player);
        Scoreboard scoreboard = player.getScoreboard();
        Team playerTeam = scoreboard.getTeam(player.getName().getString());
        if (playerTeam != null) {
            String entityScoreName = entity.getNameForScoreboard();
            scoreboard.addScoreHolderToTeam(entityScoreName, playerTeam);
        }
    }
    public static void sendFeedback(ServerPlayerEntity player, Text message) {
        player.sendMessage(message, true);
    }
    private static void spawnMissing(ServerPlayerEntity player, DroneManager.DroneData data) {
        Vec3d pos = player.getPos();
        var world = player.getWorld();

        if (getEntity(player, data.attack1) == null) {
            AttackDroneEntity e = new AttackDroneEntity(ModEntities.ATTACK_DRONE, world);
            e.setPosition(pos.add(2, 1, 0));
            world.spawnEntity(e);
            initDrones(player, e);
            data.applyRecordedHealthAndClear(e, "attack1");  // 恢复血量
            data.attack1 = e.getUuid();
        }
        if (getEntity(player, data.attack2) == null) {
            AttackDroneEntity e = new AttackDroneEntity(ModEntities.ATTACK_DRONE, world);
            e.setPosition(pos.add(-2, 1, 0));
            world.spawnEntity(e);
            initDrones(player, e);
            data.applyRecordedHealthAndClear(e, "attack2");  // 恢复血量
            data.attack2 = e.getUuid();
        }
        if (getEntity(player, data.treatment) == null) {
            TreatmentDroneEntity e = new TreatmentDroneEntity(ModEntities.TREATMENT_DRONE, world);
            e.setPosition(pos.add(0, 1, 2));
            world.spawnEntity(e);
            initDrones(player, e);
            data.applyRecordedHealthAndClear(e, "treatment");  // 恢复血量
            data.treatment = e.getUuid();
        }
    }
    private static void recordHealthIfAlive(ServerPlayerEntity player, UUID uuid, String type, DroneData data) {
        if (uuid == null) return;
        Entity e = player.getWorld().getEntity(uuid);
        if (e instanceof LivingEntity living && e.isAlive() && living.getHealth() > 0) {
            data.recordHealthOnRecall(living, type);
        }
    }
    private static void clearAllDrones(ServerPlayerEntity player, DroneManager.DroneData data) {
        // 回收前记录血量
        recordHealthIfAlive(player, data.attack1, "attack1", data);
        recordHealthIfAlive(player, data.attack2, "attack2", data);
        recordHealthIfAlive(player, data.treatment, "treatment", data);

        // 执行移除
        removeEntity(player, data.attack1);
        removeEntity(player, data.attack2);
        removeEntity(player, data.treatment);
        data.clearEntities();
        // 清空为0或null的记录
        if (data.attack1Health != null && data.attack1Health <= 0) data.attack1Health = null;
        if (data.attack2Health != null && data.attack2Health <= 0) data.attack2Health = null;
        if (data.treatmentHealth != null && data.treatmentHealth <= 0) data.treatmentHealth = null;
    }

    private static Entity getEntity(ServerPlayerEntity player, UUID uuid) {
        if (uuid == null) return null;
        return player.getWorld().getEntity(uuid);
    }

    private static void removeEntity(ServerPlayerEntity player, UUID uuid) {
        if (uuid == null) return;
        Entity e = player.getWorld().getEntity(uuid);
        if (e != null) e.discard();
    }
    public static final DroneManager INSTANCE = new DroneManager();

    private final Map<UUID, DroneData> playerDrones = new HashMap<>();

    private DroneManager() {}

    public DroneData getOrCreate(ServerPlayerEntity player) {
        return playerDrones.computeIfAbsent(player.getUuid(), uuid -> new DroneData());
    }
    public void removePlayer(UUID playerUuid) {
        DroneData data = playerDrones.get(playerUuid);
        if (data != null) {
            data.clearEntities();
            playerDrones.remove(playerUuid);
        }
    }

    public static class DroneData {
        public UUID attack1 = null;
        public UUID attack2 = null;
        public UUID treatment = null;
        // 记录上次回收时的血量
        public Float attack1Health = null;
        public Float attack2Health = null;
        public Float treatmentHealth = null;

        public int cooldownTicks = 0; // 剩余冷却时间

        public void clearEntities() {
            // 实际删除实体交给后面的清除逻辑，这里只清空UUID
            attack1 = null;
            attack2 = null;
            treatment = null;
        }

        public int countMissing() {
            int missing = 0;
            if (attack1 == null) missing++;
            if (attack2 == null) missing++;
            if (treatment == null) missing++;
            return missing;
        }

        public boolean allDead(MinecraftServer server) {
            return getEntity(server, attack1) == null &&
                    getEntity(server, attack2) == null &&
                    getEntity(server, treatment) == null;
        }
        public void recordHealthOnRecall(LivingEntity entity, String type) {
            if (!(entity instanceof DroneEntity)) return;

            float health = entity.getHealth();

            switch (type) {
                case "attack1" -> this.attack1Health = health;
                case "attack2" -> this.attack2Health = health;
                case "treatment" -> this.treatmentHealth = health;
            }
        }

        // 新增：召唤后恢复血量并清除记录
        public void applyRecordedHealthAndClear(DroneEntity entity, String type) {
            if (entity == null) return;

            Float recorded = switch (type) {
                case "attack1" -> attack1Health;
                case "attack2" -> attack2Health;
                case "treatment" -> treatmentHealth;
                default -> null;
            };

            if (recorded != null && recorded > 0) {
                entity.setHealth(recorded);
                // 用完就清掉，防止重复使用
                switch (type) {
                    case "attack1" -> attack1Health = null;
                    case "attack2" -> attack2Health = null;
                    case "treatment" -> treatmentHealth = null;
                }
            }
        }
        private Entity getEntity(MinecraftServer server, UUID uuid) {
            if (uuid == null) return null;
            for (var world : server.getWorlds()) {
                Entity e = world.getEntity(uuid);
                if (e != null) return e;
            }
            return null;
        }
    }
}
