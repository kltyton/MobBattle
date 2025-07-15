package com.kltyton.mob_battle.core;

import com.kltyton.mob_battle.Mob_battle;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
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
                mobA.setTarget(mobB);
                mobB.setTarget(mobA);

                // 添加AI强制更新（针对特殊生物）
                if (mobA.getTarget() == null) {
                    mobA.setAttacking(true);
                    mobA.getLookControl().lookAt(mobB);
                }
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