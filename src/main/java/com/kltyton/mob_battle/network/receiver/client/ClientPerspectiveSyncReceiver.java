package com.kltyton.mob_battle.network.receiver.client;

import com.kltyton.mob_battle.network.packet.PlayerSkillUtilPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

/**
 * 客户端视角同步接收器（当前为无操作）。
 *
 * <p>负责处理视角切换同步包 {@link PlayerSkillUtilPayload}。目前所有视角切换分支均被注释，
 * 接收器仅保留空 switch 结构作为无操作占位，便于未来重新启用；行为与拆分前保持一致。</p>
 */
public final class ClientPerspectiveSyncReceiver {

    private ClientPerspectiveSyncReceiver() {
    }

    /**
     * 注册视角同步接收器（PlayerSkillUtilPayload）。
     */
    public static void registerPlayerSkillUtil() {
        ClientPlayNetworking.registerGlobalReceiver(PlayerSkillUtilPayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            if (client.player != null) {
                switch (payload.name()) {
/*                        case "setPerson_1" -> client.options.setPerspective(Perspective.FIRST_PERSON);
                        case "setPerson_2" -> client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
                        case "setPerson_3" -> client.options.setPerspective(Perspective.THIRD_PERSON_FRONT);*/
                }
            }
        });
    }
}
