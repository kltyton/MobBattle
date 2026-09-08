package com.kltyton.mob_battle.entity.vindicatorgeneral;

import com.kltyton.mob_battle.client.render.GeoRenderTransforms;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class VindicatorGeneralEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<VindicatorGeneralEntity, R> {

    public VindicatorGeneralEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new VindicatorGeneralEntityModel());
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        GeoRenderTransforms.applyHeadRotation(renderPassInfo, snapshots, "Head", false);
    }

}
