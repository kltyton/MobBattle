package com.kltyton.mob_battle.client.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.util.Mth;

/**
 * GeckoLib 骨骼渲染变换。
 *
 * <p>只读取当前渲染状态并写入骨骼快照，不持有跨帧状态。</p>
 */
public final class GeoRenderTransforms {
    private GeoRenderTransforms() {
    }

    /** 将实体俯仰角和偏航角转换为头部骨骼旋转。 */
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
