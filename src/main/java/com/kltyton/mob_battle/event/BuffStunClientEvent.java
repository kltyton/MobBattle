package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.effect.ModEffects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Environment(EnvType.CLIENT)
public class BuffStunClientEvent {
    record PlayerStunState(Vec3d position, float yaw, float pitch) {
    }
    private static final Map<UUID, PlayerStunState> STUN_MAP = new HashMap<>();

    public static void ClientInit() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PlayerEntity player = client.player;
            if (player == null) return;

            UUID id = player.getUuid();

            // 被眩晕中
            if (player.hasStatusEffect(ModEffects.STUN_ENTRY)) {
                if (!STUN_MAP.containsKey(id)) {
                    STUN_MAP.put(id, new BuffStunClientEvent.PlayerStunState(player.getPos(), player.getYaw(), player.getPitch()));
                }

                // 获取记录
                BuffStunClientEvent.PlayerStunState state = STUN_MAP.get(id);
                if (state != null) {
                    player.setVelocity(Vec3d.ZERO);
                    player.setPosition(state.position);
                    player.setYaw(state.yaw);
                    player.setPitch(state.pitch);
                    player.setHeadYaw(state.yaw);
                }

            } else {
                STUN_MAP.remove(id);
            }
        });
    }
}
