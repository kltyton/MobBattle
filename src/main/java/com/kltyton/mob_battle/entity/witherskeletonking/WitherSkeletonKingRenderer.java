package com.kltyton.mob_battle.entity.witherskeletonking;

import com.kltyton.mob_battle.client.render.GeoRenderUtil;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;

public class WitherSkeletonKingRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<WitherSkeletonKingEntity, R> {
    public static final DataTicket<Boolean> CAN_HALO = DataTicket.create("can_halo", Boolean.class);
    public WitherSkeletonKingRenderer(EntityRendererProvider.Context context) {
        super(context, new WitherSkeletonKingEntityModel());
        this.withRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        GeoRenderUtil.applyHeadRotation(renderPassInfo, snapshots, "Head", false);

        Boolean canHalo = renderPassInfo.renderState().getOrDefaultGeckolibData(CAN_HALO, false);
        snapshots.ifPresent("quan", snapshot -> {
            boolean hidden = canHalo == null || !canHalo;
            snapshot.skipRender(hidden).skipChildrenRender(hidden);
        });
    }
}
