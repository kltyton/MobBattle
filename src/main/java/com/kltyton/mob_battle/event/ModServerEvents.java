package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.event.masterscepter.MasterScepterManager;
import com.kltyton.mob_battle.event.player.DeathPenaltyEvents;
import com.kltyton.mob_battle.items.tool.snipe.VsSnipe;
import com.kltyton.mob_battle.items.tool.sword.BloodKnifeItem;
import com.kltyton.mob_battle.items.tool.sword.PoisonKnifeItem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;

public class ModServerEvents {
    public static void init() {
        MasterScepterManager.initLifecycle();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Mob_battle.SERVER = server;
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            BloodKnifeItem.clearAllPending(server);
            PoisonKnifeItem.clearAllPending(server);
            DeathPenaltyEvents.clearServer(server);
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                VsSnipe.clearPlayerState(player);
            }
            Mob_battle.SERVER = null;
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            BloodKnifeItem.clearPending(server, handler.getPlayer().getUUID());
            PoisonKnifeItem.clearPending(server, handler.getPlayer().getUUID());
            DeathPenaltyEvents.clearPlayer(server, handler.getPlayer().getUUID());
            VsSnipe.clearPlayerState(handler.getPlayer());
        });

        //友伤关闭
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            // 仅在服务器端处理，且攻击者必须是玩家
            if (!world.isClientSide() && player instanceof ServerPlayer attacker) {
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
