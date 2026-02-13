package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class ModMainEvents {
    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Mob_battle.SERVER = server;
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            Mob_battle.SERVER = null;
        });
        // 在你的 Mod 初始化类中注册
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            // 获取旧玩家身上的等级
            StatusEffectInstance oldEffect = oldPlayer.getStatusEffect(ModEffects.HEART_EATER_ENTRY);
            int nextLevel = 0;

            if (oldEffect != null) {
                int currentLevel = oldEffect.getAmplifier();
                // 如果当前有效果，等级 +1，最高到 V 级 (index 4)
                nextLevel = Math.min(currentLevel + 1, 4);
            }

            // 给新玩家施加 3 分钟 (3600 ticks) 的效果
            newPlayer.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.MINING_FATIGUE,
                    3600,
                    nextLevel
            ));
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            // 仅在服务器端处理，且攻击者必须是玩家
            if (!world.isClient && player instanceof ServerPlayerEntity attacker) {
                if (entity instanceof LivingEntity target) {
                    if (attacker.isTeammate(target)) {
                        return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}
