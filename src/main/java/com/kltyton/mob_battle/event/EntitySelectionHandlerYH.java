package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.Mob_battle;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class EntitySelectionHandlerYH {
    private static final Map<PlayerEntity, UUID> selectedEntityA = new WeakHashMap<>();
    private static final Map<PlayerEntity, UUID> selectedEntityB = new WeakHashMap<>();

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
                // 特殊处理坚守者
                if (mobA instanceof WardenEntity wardenA) {
                    forceWardenTarget(wardenA, mobB, world);
                } else {
                    mobA.setTarget(mobB);
                }

                if (mobB instanceof WardenEntity wardenB) {
                    forceWardenTarget(wardenB, mobA, world);
                } else {
                    mobB.setTarget(mobA);
                }

                // 添加AI强制更新（针对特殊生物）
                if (mobA.getTarget() == null) {
                    mobA.setAttacking(true);
                    mobA.getLookControl().lookAt(mobB);
                }
                // 清除选择
            }
            selectedEntityA.remove(player);
            selectedEntityB.remove(player);
        }
    }
    // 强制设置坚守者目标
    public static void forceWardenTarget(WardenEntity warden, LivingEntity target, World world) {
        if (world.isClient) return;
        if (warden.getWorld().isClient) return;
        // 设置强制攻击标志
        warden.dataTracker.set(DataTrackers.FORCED_ATTACK_FLAG, true);
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