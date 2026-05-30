package com.kltyton.mob_battle.entity.xunsheng;

import com.kltyton.mob_battle.client.render.GeoRenderUtil;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

public class XunShengEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<XunShengEntity, R> {
    public XunShengEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new XunShengEntityModel());
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        GeoRenderUtil.applyHeadRotation(renderPassInfo, snapshots, "Head", true);
    }
}
