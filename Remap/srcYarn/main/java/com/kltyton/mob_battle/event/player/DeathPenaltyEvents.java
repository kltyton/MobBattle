package com.kltyton.mob_battle.event.player;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.player.IPlayerEntityAccessor;
import com.kltyton.mob_battle.items.misc.CardiotonicInjectionItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathPenaltyEvents {
    private static final Map<UUID, Integer> DEATH_PENALTY_CACHE = new HashMap<>();
    public static void init() {
        //死亡惩罚事件
        // 1. 监听死亡事件：在玩家点击重生前记录数据
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity player) {
                if (((IPlayerEntityAccessor)player).isUsingGeckoLib()) {
                    ((IPlayerEntityAccessor)player).setUseGeckoLib(false);
                    var maxHealth = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
                    var scaleAttr = player.getAttributeInstance(EntityAttributes.SCALE);
                    var speedAttr = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
                    var jumpAttr = player.getAttributeInstance(EntityAttributes.JUMP_STRENGTH);
                    var reachAttr = player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
                    var block_interaction_range = player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE);
                    var safe_fall_distance = player.getAttributeInstance(EntityAttributes.SAFE_FALL_DISTANCE);

                    if (scaleAttr != null) {
                        scaleAttr.removeModifier(CardiotonicInjectionItem.CARDIOTONIC_MODIFIER_ID);
                    }
                    if (speedAttr != null) {
                        speedAttr.removeModifier(CardiotonicInjectionItem.CARDIOTONIC_MODIFIER_ID);
                    }
                    if (jumpAttr != null) {
                        jumpAttr.removeModifier(CardiotonicInjectionItem.CARDIOTONIC_MODIFIER_ID);
                    }
                    if (maxHealth != null) {
                        maxHealth.removeModifier(CardiotonicInjectionItem.CARDIOTONIC_MODIFIER_ID);
                    }
                    if (reachAttr != null) {
                        reachAttr.removeModifier(CardiotonicInjectionItem.CARDIOTONIC_MODIFIER_ID);
                    }
                    if (block_interaction_range != null) {
                        block_interaction_range.removeModifier(CardiotonicInjectionItem.CARDIOTONIC_MODIFIER_ID);
                    }
                    if (safe_fall_distance != null) {
                        safe_fall_distance.removeModifier(CardiotonicInjectionItem.CARDIOTONIC_MODIFIER_ID);
                    }
                }

                StatusEffectInstance effect = player.getStatusEffect(ModEffects.HEART_EATER_ENTRY);
                if (effect != null) {
                    // 如果死亡时有效果，计算下一级
                    int nextLevel = Math.min(effect.getAmplifier() + 1, 4);
                    DEATH_PENALTY_CACHE.put(player.getUuid(), nextLevel);
                } else {
                    // 如果死亡时没有效果，重置死亡次数/等级为 0
                    DEATH_PENALTY_CACHE.put(player.getUuid(), 0);
                }
            }
        });

        // 2. 监听重生事件：施加缓存的效果
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            UUID uuid = newPlayer.getUuid();

            // 从缓存中取出等级（remove 会直接取出并从 Map 中删除，防止内存泄漏）
            Integer nextLevel = DEATH_PENALTY_CACHE.remove(uuid);

            if (nextLevel != null) {
                // 给新玩家施加效果
                newPlayer.addStatusEffect(new StatusEffectInstance(
                        ModEffects.HEART_EATER_ENTRY,
                        3600, // 3分钟
                        nextLevel
                ));
            }
        });
    }
}
