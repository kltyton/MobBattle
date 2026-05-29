package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.effect.ModEffects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Environment(EnvType.CLIENT)
public class BuffStunClientEvent {
    record PlayerStunState(Vec3 position, float yaw, float pitch) {
    }
    private static final Map<UUID, PlayerStunState> STUN_MAP = new HashMap<>();

    public static void ClientInit() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Player player = client.player;
            if (player == null || player.isCreative() || player.isSpectator()) return;
            UUID id = player.getUUID();
            // 被眩晕中
            if (player.hasEffect(ModEffects.STUN_ENTRY)) {
                if (!STUN_MAP.containsKey(id)) {
                    STUN_MAP.put(id, new BuffStunClientEvent.PlayerStunState(player.position(), player.getYRot(), player.getXRot()));
                }

                // 获取记录
                BuffStunClientEvent.PlayerStunState state = STUN_MAP.get(id);
                if (state != null) {
                    player.setDeltaMovement(Vec3.ZERO);
                    player.setPos(state.position);
                    player.setYRot(state.yaw);
                    player.setXRot(state.pitch);
                    player.setYHeadRot(state.yaw);
                }

            } else {
                STUN_MAP.remove(id);
            }
        });
    }
}
