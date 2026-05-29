package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.Mob_battle;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;

public class ModServerEvents {
    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Mob_battle.SERVER = server;
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            Mob_battle.SERVER = null;
        });

        //友伤关闭
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            // 仅在服务器端处理，且攻击者必须是玩家
            if (!world.isClientSide && player instanceof ServerPlayer attacker) {
                if (entity instanceof LivingEntity target) {
                    if (attacker.isAlliedTo(target)) {
                        return InteractionResult.FAIL;
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }
}
