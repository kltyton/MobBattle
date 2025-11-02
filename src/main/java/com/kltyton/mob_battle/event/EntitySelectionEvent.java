package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.items.ModItems;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.Angriness;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class EntitySelectionEvent {
    private static final Map<PlayerEntity, UUID> selectedEntityA = new WeakHashMap<>();
    private static final Map<PlayerEntity, UUID> selectedEntityB = new WeakHashMap<>();

    public static void init() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;
            if (isHoldingStick(player)) {
                handleLeftClick(player, entity);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;
            if (isHoldingStick(player)) {
                return handleRightClick(player, entity, world);
            }
            return ActionResult.PASS;
        });
    }

    private static boolean isHoldingStick(PlayerEntity player) {
        return player.getMainHandStack().getItem() == ModItems.MUTUAL_ATTACK_STICK;
    }

    private static void handleLeftClick(PlayerEntity player, Entity entity) {
        if (isValidTarget(entity)) {
            selectedEntityA.put(player, entity.getUuid());
            player.sendMessage(Text.literal("实体A已选择: " + entity.getType().toString() +
                    (entity instanceof PlayerEntity ? " (玩家)" : "")), true);
        } else {
            player.sendMessage(Text.literal("实体A选择无效"), true);
        }
    }

    private static ActionResult handleRightClick(PlayerEntity player, Entity entity, World world) {
        if (isValidTarget(entity)) {
            selectedEntityB.put(player, entity.getUuid());
            player.sendMessage(Text.literal("实体B已选择: " + entity.getType().toString() +
                    (entity instanceof PlayerEntity ? " (玩家)" : "")), true);
            tryStartMutualAttack(player, world);
            return ActionResult.SUCCESS;
        } else {
            player.sendMessage(Text.literal("实体B选择无效"), true);
            return ActionResult.PASS;
        }
    }

    private static boolean isValidTarget(Entity entity) {
        return entity instanceof MobEntity mob && mob.canTakeDamage() && mob.isAlive()
                || entity instanceof PlayerEntity;
    }

    private static void tryStartMutualAttack(PlayerEntity player, World world) {
        if (selectedEntityA.containsKey(player) && selectedEntityB.containsKey(player)) {
            Entity entityA = world.getEntity(selectedEntityA.get(player));
            Entity entityB = world.getEntity(selectedEntityB.get(player));

            if (entityA == null || entityB == null) {
                player.sendMessage(Text.literal("选择的实体不存在"), true);
                selectedEntityA.remove(player);
                selectedEntityB.remove(player);
                return;
            }

            // 双方都是 MobEntity → 互相攻击
            if (entityA instanceof MobEntity mobA && entityB instanceof MobEntity mobB) {
                setMobTarget(mobA, mobB, world);
                setMobTarget(mobB, mobA, world);
            }
            // A 是玩家，B 是 Mob → 只让 MobB 攻击玩家A
            else if (entityA instanceof PlayerEntity && entityB instanceof MobEntity mobB) {
                setMobTarget(mobB, (PlayerEntity) entityA, world);
            }
            // B 是玩家，A 是 Mob → 只让 MobA 攻击玩家B
            else if (entityB instanceof PlayerEntity && entityA instanceof MobEntity mobA) {
                setMobTarget(mobA, (PlayerEntity) entityB, world);
            }

            selectedEntityA.remove(player);
            selectedEntityB.remove(player);
        }
    }

    private static void setMobTarget(MobEntity mob, LivingEntity target, World world) {
        if (mob instanceof WardenEntity warden) {
            forceWardenTarget(warden, target, world);
        } else {
            mob.setTarget(target);
            mob.setAttacking(true);
            mob.getLookControl().lookAt(target);
        }
    }

    public static void forceWardenTarget(WardenEntity warden, LivingEntity target, World world) {
        if (world.isClient) return;

        // 设置强制攻击标志
        warden.dataTracker.set(DataTrackersEvent.FORCED_ATTACK_FLAG, true);

        // 更新攻击目标
        warden.updateAttackTarget(target);

        // 设置愤怒值到攻击阈值
        warden.increaseAngerAt(target, Angriness.ANGRY.getThreshold() + 20, false);

        // 强制大脑更新
        Brain<WardenEntity> brain = warden.getBrain();
        brain.forget(MemoryModuleType.ROAR_TARGET);
        brain.remember(MemoryModuleType.ATTACK_TARGET, target);
        brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);

        // 重置姿势
        warden.setPose(EntityPose.STANDING);
    }
}
