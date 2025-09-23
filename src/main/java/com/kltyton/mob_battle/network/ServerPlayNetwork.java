package com.kltyton.mob_battle.network;

import com.kltyton.mob_battle.entity.highbird.HighbirdBaseEntity;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import com.kltyton.mob_battle.network.packet.HighbirdAngerPayload;
import com.kltyton.mob_battle.network.packet.HighbirdAttackPayload;
import com.kltyton.mob_battle.network.packet.KeepInventoryPayload;
import com.kltyton.mob_battle.utils.HeadStoneUtils;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public class ServerPlayNetwork {
    public static void init() {
        PayloadTypeRegistry.playC2S().register(
                HighbirdAttackPayload.ID,
                HighbirdAttackPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                HighbirdAngerPayload.ID,
                HighbirdAngerPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                KeepInventoryPayload.ID,
                KeepInventoryPayload.CODEC
        );
        // 注册服务器端接收器
        ServerPlayNetworking.registerGlobalReceiver(HighbirdAttackPayload.ID,
                (payload, context) -> {
                    MinecraftServer server = context.server();
                    server.execute(() -> {
                        Entity attacker = context.player().getWorld().getEntityById(payload.attackerId());
                        if (attacker instanceof HighbirdBaseEntity highbird && highbird.getWorld() instanceof ServerWorld serverWorld)
                            highbird.performAttack(serverWorld, highbird.getTarget());
                    });
                }
        );
        // 注册服务器端接收器
        ServerPlayNetworking.registerGlobalReceiver(HighbirdAngerPayload.ID,
                (payload, context) -> {
                    MinecraftServer server = context.server();
                    server.execute(() -> {
                        Entity anger = context.player().getWorld().getEntityById(payload.angerId());
                        if (anger instanceof HighbirdAdulthoodEntity highbird && highbird.getWorld() instanceof ServerWorld serverWorld)
                            highbird.setAiDisabled(false);
                    });
                }
        );
        ServerPlayNetworking.registerGlobalReceiver(KeepInventoryPayload.ID,
                (payload, context) -> {
                    MinecraftServer server = context.server();
                    server.execute(() -> {
                        Entity entity = context.player().getWorld().getEntityById(payload.keeperId());
                        if (entity != null) {
                            HeadStoneUtils.setKeep(entity.getUuid(), payload.isKeep());
                        }
                    });
                }
        );
    }
}
