package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class EntitySelectionEvent {
    private static final Map<Player, UUID> selectedEntityA = new WeakHashMap<>();
    private static final Map<Player, UUID> selectedEntityB = new WeakHashMap<>();

    public static void init() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide) return InteractionResult.PASS;
            if (isHoldingStick(player)) {
                handleLeftClick(player, entity);
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide) return InteractionResult.PASS;
            if (isHoldingStick(player)) {
                return handleRightClick(player, entity, world);
            }
            return InteractionResult.PASS;
        });
    }

    private static boolean isHoldingStick(Player player) {
        return player.getMainHandItem().getItem() == ModItems.MUTUAL_ATTACK_STICK;
    }

    private static void handleLeftClick(Player player, Entity entity) {
        if (isValidTarget(entity)) {
            selectedEntityA.put(player, entity.getUUID());
            player.displayClientMessage(Component.literal("实体A已选择: " + entity.getType().toString() +
                    (entity instanceof Player ? " (玩家)" : "")), true);
        } else {
            player.displayClientMessage(Component.literal("实体A选择无效"), true);
        }
    }

    private static InteractionResult handleRightClick(Player player, Entity entity, Level world) {
        if (isValidTarget(entity)) {
            selectedEntityB.put(player, entity.getUUID());
            player.displayClientMessage(Component.literal("实体B已选择: " + entity.getType().toString() +
                    (entity instanceof Player ? " (玩家)" : "")), true);
            tryStartMutualAttack(player, world);
            return InteractionResult.SUCCESS;
        } else {
            player.displayClientMessage(Component.literal("实体B选择无效"), true);
            return InteractionResult.PASS;
        }
    }

    private static boolean isValidTarget(Entity entity) {
        return entity instanceof Mob mob && mob.canBeSeenAsEnemy() && mob.isAlive()
                || entity instanceof Player;
    }

    private static void tryStartMutualAttack(Player player, Level world) {
        if (selectedEntityA.containsKey(player) && selectedEntityB.containsKey(player)) {
            Entity entityA = world.getEntity(selectedEntityA.get(player));
            Entity entityB = world.getEntity(selectedEntityB.get(player));

            if (entityA == null || entityB == null) {
                player.displayClientMessage(Component.literal("选择的实体不存在"), true);
                selectedEntityA.remove(player);
                selectedEntityB.remove(player);
                return;
            }

            // 双方都是 MobEntity → 互相攻击
            if (entityA instanceof Mob mobA && entityB instanceof Mob mobB) {
                setMobTarget(mobA, mobB, world);
                setMobTarget(mobB, mobA, world);
            }
            // A 是玩家，B 是 Mob → 只让 MobB 攻击玩家A
            else if (entityA instanceof Player && entityB instanceof Mob mobB) {
                setMobTarget(mobB, (Player) entityA, world);
            }
            // B 是玩家，A 是 Mob → 只让 MobA 攻击玩家B
            else if (entityB instanceof Player && entityA instanceof Mob mobA) {
                setMobTarget(mobA, (Player) entityB, world);
            }

            selectedEntityA.remove(player);
            selectedEntityB.remove(player);
        }
    }

    private static void setMobTarget(Mob mob, LivingEntity target, Level world) {
        if (mob instanceof Warden warden) {
            forceWardenTarget(warden, target, world);
        } else {
            mob.setTarget(target);
            mob.setAggressive(true);
            mob.getLookControl().setLookAt(target);
        }
    }

    public static void forceWardenTarget(Warden warden, LivingEntity target, Level world) {
        if (world.isClientSide) return;

        // 设置强制攻击标志
        warden.getEntityData().set(DataTrackersEvent.FORCED_ATTACK_FLAG, true);

        // 更新攻击目标
        warden.setAttackTarget(target);

        // 设置愤怒值到攻击阈值
        warden.increaseAngerAt(target, AngerLevel.ANGRY.getMinimumAnger() + 20, false);

        // 强制大脑更新
        Brain<Warden> brain = warden.getBrain();
        brain.eraseMemory(MemoryModuleType.ROAR_TARGET);
        brain.setMemory(MemoryModuleType.ATTACK_TARGET, target);
        brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);

        // 重置姿势
        warden.setPose(Pose.STANDING);
    }
}
