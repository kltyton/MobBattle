package com.kltyton.mob_battle.network.receiver.client;

import com.kltyton.mob_battle.accessor.ILead;
import com.kltyton.mob_battle.network.packet.ILeadUpdatePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

/**
 * 客户端实体状态接收器。
 *
 * <p>负责处理牵引绳状态包 {@link ILeadUpdatePayload}：在客户端当前维度中解析目标实体，
 * 并在 Fabric 已保证的渲染线程回调中写入通用牵引绳状态。对实体类型使用 instanceof 校验，
 * 避免对不实现 {@link ILead} 的实体进行未经检查的强制转换。</p>
 */
public final class ClientEntityStateReceivers {

    private ClientEntityStateReceivers() {
    }

    /**
     * 注册牵引绳状态接收器（ILeadUpdatePayload）。
     */
    public static void registerILeadUpdate() {
        ClientPlayNetworking.registerGlobalReceiver(ILeadUpdatePayload.ID, (payload, context) -> {
            Minecraft client = context.client();
            Entity entity = client.level == null ? null : client.level.getEntity(payload.entityId());
            int iLead_1 = payload.iLead_1();
            int iLead_2 = payload.iLead_2();
            if (entity instanceof ILead lead) {
                if (iLead_1 != 3) lead.setIsUniversalLeadEnyity(iLead_1 == 1);
                if (iLead_2 != 3) lead.setIsInvisibleUniversalLeadEnyity(iLead_2 == 1);
            }
        });
    }
}
