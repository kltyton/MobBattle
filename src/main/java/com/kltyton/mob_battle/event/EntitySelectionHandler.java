package com.kltyton.mob_battle.core;

import com.kltyton.mob_battle.Mob_battle;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import java.util.*;

public class EntitySelectionHandler {
    private static final Map<PlayerEntity, UUID> selectedEntityA = new WeakHashMap<>();
    private static final Map<PlayerEntity, UUID> selectedEntityB = new WeakHashMap<>();
    // 新增战斗配对存储（存储双方UUID）
    private static final Map<UUID, UUID> combatPairs = new HashMap<>();

    public static void registerEvents() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient) return ActionResult.PASS; // 添加客户端校验
            if (isHoldingStick(player)) {
                handleLeftClick(player, entity);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient) return ActionResult.PASS; // 添加客户端校验
            if (isHoldingStick(player)) {
                return handleRightClick(player, entity, world);
            }
            return ActionResult.PASS;
        });
        // 注册服务器每tick检查（20 ticks = 1秒）
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (server.getTicks() % 60 == 0) {
                checkAndUpdateTargets(server);
            }
        });
    }
    // 新增持续目标锁定逻辑
    private static void checkAndUpdateTargets(MinecraftServer server) {
        // 创建临时列表存储需要处理的配对
        List<UUID> toRemove = new ArrayList<>();

        // 使用 entries 的副本进行遍历
        new HashMap<>(combatPairs).entrySet().forEach(entry -> {
            UUID attackerId = entry.getKey();
            UUID targetId = entry.getValue();

            Entity attacker = findEntity(server, attackerId);
            Entity target = findEntity(server, targetId);

            if (!validatePair(attacker, target)) {
                // 标记需要移除的配对
                toRemove.add(attackerId);
                toRemove.add(targetId);
            } else {
                forceCombat((MobEntity) attacker, (MobEntity) target);
            }
        });

        // 批量移除无效配对
        toRemove.forEach(uuid -> {
            combatPairs.remove(uuid);
            combatPairs.values().removeIf(value -> value.equals(uuid));
        });
    }

    // 强制战斗逻辑
    private static void forceCombat(MobEntity mob, MobEntity target) {
        // 核心目标锁定
        mob.setTarget(target);
        // 增强AI控制
        mob.setAttacking(true);
        mob.getLookControl().lookAt(target);
    }
    // 实体搜索方法（跨维度）
    private static Entity findEntity(MinecraftServer server, UUID uuid) {
        for (ServerWorld world : server.getWorlds()) {
            Entity entity = world.getEntity(uuid);
            if (entity != null) return entity;
        }
        return null;
    }

    private static boolean isHoldingStick(PlayerEntity player) {
        return player.getMainHandStack().getItem() == Mob_battle.MUTUAL_ATTACK_STICK;
    }

    private static void handleLeftClick(PlayerEntity player, Entity entity) {
        if (isValidTarget(entity)) {
            selectedEntityA.put(player, entity.getUuid());
            player.sendMessage(Text.literal("实体A已选择" + entity.getType().toString()), true);
        }
    }

    private static ActionResult handleRightClick(PlayerEntity player, Entity entity, World world) {
        if (isValidTarget(entity)) {
            selectedEntityB.put(player, entity.getUuid());
            player.sendMessage(Text.literal("实体B已选择" + entity.getType().toString()), true);
            tryStartMutualAttack(player, world);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private static boolean isValidTarget(Entity entity) {
        // 更精确的验证逻辑
        return entity instanceof MobEntity mob && mob.canTakeDamage() && mob.isAlive();
    }
    // 验证战斗配对有效性
    private static boolean validatePair(Entity attacker, Entity target) {
        return attacker instanceof MobEntity
                && target instanceof MobEntity
                && attacker.isAlive()
                && !attacker.isRemoved()
                && target.isAlive()
                && !target.isRemoved();
    }

    private static void tryStartMutualAttack(PlayerEntity player, World world) {
        if (selectedEntityA.containsKey(player) && selectedEntityB.containsKey(player)) {
            Entity entityA = world.getEntity(selectedEntityA.get(player));
            Entity entityB = world.getEntity(selectedEntityB.get(player));

            // 添加空值检查和类型校验
            if (entityA instanceof MobEntity mobA &&
                    entityB instanceof MobEntity mobB &&
                    !entityA.isRemoved() &&
                    !entityB.isRemoved()) {

                // 强制设置攻击目标
                mobA.setTarget(mobB);
                mobB.setTarget(mobA);

                // 添加AI强制更新（针对特殊生物）
                if (mobA.getTarget() == null) {
                    mobA.setAttacking(true);
                    mobA.getLookControl().lookAt(mobB);
                }
                combatPairs.put(mobA.getUuid(), mobB.getUuid());
                combatPairs.put(mobB.getUuid(), mobA.getUuid());
                // 初始锁定
                forceCombat(mobA, mobB);
                forceCombat(mobB, mobA);
                // 清除选择
                selectedEntityA.remove(player);
                selectedEntityB.remove(player);
            } else {
                selectedEntityA.remove(player);
                selectedEntityB.remove(player);
            }
        }
    }
}
