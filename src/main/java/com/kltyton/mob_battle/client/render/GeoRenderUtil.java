package com.kltyton.mob_battle.client.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.util.Mth;

public final class GeoRenderUtil {
    private GeoRenderUtil() {
    }

    public static <R extends GeoRenderState> void applyHeadRotation(
            RenderPassInfo<R> renderPassInfo,
            BoneSnapshots snapshots,
            String headBone,
            boolean invertPitch
    ) {
        snapshots.get(headBone).ifPresent(snapshot -> {
            float pitch = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0.0F);
            float yaw = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0.0F);

            snapshot.setRotX((invertPitch ? -pitch : pitch) * Mth.DEG_TO_RAD);
            snapshot.setRotY(-yaw * Mth.DEG_TO_RAD);
        });
    }
}
